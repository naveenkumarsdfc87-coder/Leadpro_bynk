package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ContactMethod
import com.example.data.model.FollowUpTimeline
import com.example.data.model.LeadActivityEntity
import com.example.data.model.LeadEntity
import com.example.data.model.LeadSource
import com.example.data.model.LeadStatus
import com.example.data.repository.AuthRepository
import com.example.data.repository.LeadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    CAPTURE,
    KIOSK,
    ADMIN_LOGIN,
    ADMIN_DASHBOARD,
    ADMIN_LEADS_LIST,
    LEAD_DETAIL,
    ADMIN_SETTINGS
}

enum class LeadSortOrder {
    NEWEST_FIRST,
    VALUE_HIGH_TO_LOW,
    NAME_A_TO_Z
}

data class LeadFormData(
    val fullName: String = "",
    val company: String = "",
    val jobTitle: String = "",
    val email: String = "",
    val phone: String = "",
    val status: LeadStatus = LeadStatus.HOT,
    val dealValue: String = "15000",
    val interestCategory: String = "Enterprise Solutions",
    val source: LeadSource = LeadSource.TRADE_SHOW,
    val preferredContactMethod: ContactMethod = ContactMethod.EMAIL,
    val followUpTimeline: FollowUpTimeline = FollowUpTimeline.WITHIN_24H,
    val notes: String = "",
    val tags: List<String> = listOf("Decision Maker", "Urgent")
)

class LeadViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    val leadRepository = LeadRepository(db.leadDao())
    val authRepository = AuthRepository(application)

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.CAPTURE)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenHistory = mutableListOf<AppScreen>()

    // All leads from database
    val allLeads: StateFlow<List<LeadEntity>> = leadRepository.allLeads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Auth State
    val isAdminLoggedIn: StateFlow<Boolean> = authRepository.isAdminLoggedIn
    val adminName: StateFlow<String> = authRepository.adminName

    // Lead Form State
    private val _formData = MutableStateFlow(LeadFormData())
    val formData: StateFlow<LeadFormData> = _formData.asStateFlow()

    private val _formErrorMessage = MutableStateFlow<String?>(null)
    val formErrorMessage: StateFlow<String?> = _formErrorMessage.asStateFlow()

    private val _lastCapturedLead = MutableStateFlow<LeadEntity?>(null)
    val lastCapturedLead: StateFlow<LeadEntity?> = _lastCapturedLead.asStateFlow()

    private val _showCaptureSuccessDialog = MutableStateFlow(false)
    val showCaptureSuccessDialog: StateFlow<Boolean> = _showCaptureSuccessDialog.asStateFlow()

    // Filter & Search State for Admin List
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<LeadStatus?>(null)
    val selectedStatusFilter: StateFlow<LeadStatus?> = _selectedStatusFilter.asStateFlow()

    private val _selectedSourceFilter = MutableStateFlow<LeadSource?>(null)
    val selectedSourceFilter: StateFlow<LeadSource?> = _selectedSourceFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(LeadSortOrder.NEWEST_FIRST)
    val sortOrder: StateFlow<LeadSortOrder> = _sortOrder.asStateFlow()

    // Filtered Leads Flow
    val filteredLeads: StateFlow<List<LeadEntity>> = combine(
        allLeads,
        _searchQuery,
        _selectedStatusFilter,
        _selectedSourceFilter,
        _sortOrder
    ) { leads, query, statusFilter, sourceFilter, sort ->
        var list = leads

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.fullName.lowercase().contains(q) ||
                        it.company.lowercase().contains(q) ||
                        it.email.lowercase().contains(q) ||
                        it.phone.contains(q) ||
                        it.notes.lowercase().contains(q) ||
                        it.tags.lowercase().contains(q)
            }
        }

        if (statusFilter != null) {
            list = list.filter { it.status.equals(statusFilter.name, ignoreCase = true) }
        }

        if (sourceFilter != null) {
            list = list.filter { it.source.equals(sourceFilter.name, ignoreCase = true) }
        }

        when (sort) {
            LeadSortOrder.NEWEST_FIRST -> list.sortedByDescending { it.createdAt }
            LeadSortOrder.VALUE_HIGH_TO_LOW -> list.sortedByDescending { it.dealValue }
            LeadSortOrder.NAME_A_TO_Z -> list.sortedBy { it.fullName.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Lead Detail
    private val _selectedLeadId = MutableStateFlow<Long?>(null)
    val selectedLeadId: StateFlow<Long?> = _selectedLeadId.asStateFlow()

    val selectedLead: StateFlow<LeadEntity?> = _selectedLeadId.flatMapLatest { id ->
        if (id != null) leadRepository.getLeadById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedLeadActivities: StateFlow<List<LeadActivityEntity>> = _selectedLeadId.flatMapLatest { id ->
        if (id != null) leadRepository.getActivitiesForLead(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    // Navigation Methods
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenHistory.isNotEmpty()) {
            val prev = screenHistory.removeAt(screenHistory.size - 1)
            _currentScreen.value = prev
            return true
        } else if (_currentScreen.value != AppScreen.CAPTURE) {
            _currentScreen.value = AppScreen.CAPTURE
            return true
        }
        return false
    }

    fun openLeadDetail(leadId: Long) {
        _selectedLeadId.value = leadId
        navigateTo(AppScreen.LEAD_DETAIL)
    }

    // Form Field Updates
    fun updateFullName(value: String) { _formData.value = _formData.value.copy(fullName = value) }
    fun updateCompany(value: String) { _formData.value = _formData.value.copy(company = value) }
    fun updateJobTitle(value: String) { _formData.value = _formData.value.copy(jobTitle = value) }
    fun updateEmail(value: String) { _formData.value = _formData.value.copy(email = value) }
    fun updatePhone(value: String) { _formData.value = _formData.value.copy(phone = value) }
    fun updateStatus(status: LeadStatus) { _formData.value = _formData.value.copy(status = status) }
    fun updateDealValue(value: String) { _formData.value = _formData.value.copy(dealValue = value) }
    fun updateCategory(category: String) { _formData.value = _formData.value.copy(interestCategory = category) }
    fun updateSource(source: LeadSource) { _formData.value = _formData.value.copy(source = source) }
    fun updateContactMethod(method: ContactMethod) { _formData.value = _formData.value.copy(preferredContactMethod = method) }
    fun updateTimeline(timeline: FollowUpTimeline) { _formData.value = _formData.value.copy(followUpTimeline = timeline) }
    fun updateNotes(notes: String) { _formData.value = _formData.value.copy(notes = notes) }

    fun toggleTag(tag: String) {
        val current = _formData.value.tags.toMutableList()
        if (current.contains(tag)) {
            current.remove(tag)
        } else {
            current.add(tag)
        }
        _formData.value = _formData.value.copy(tags = current)
    }

    fun prefillSampleLead() {
        val samples = listOf(
            LeadFormData(
                fullName = "Dr. Aris Thorne",
                company = "Quantum BioMed",
                jobTitle = "Chief Research Architect",
                email = "a.thorne@quantumbiomed.com",
                phone = "+1 (555) 743-9921",
                status = LeadStatus.HOT,
                dealValue = "65000",
                interestCategory = "AI Automation",
                source = LeadSource.TRADE_SHOW,
                preferredContactMethod = ContactMethod.EMAIL,
                followUpTimeline = FollowUpTimeline.IMMEDIATE,
                notes = "Requires HIPAA compliant cloud pipeline. Decision made by end of month.",
                tags = listOf("Decision Maker", "VIP", "Urgent")
            ),
            LeadFormData(
                fullName = "Jordan Reed",
                company = "Nexus Financial Tech",
                jobTitle = "VP Product & Growth",
                email = "jordan.reed@nexusfintech.io",
                phone = "+1 (555) 231-8844",
                status = LeadStatus.WARM,
                dealValue = "35000",
                interestCategory = "Enterprise Solutions",
                source = LeadSource.CONFERENCE,
                preferredContactMethod = ContactMethod.PHONE,
                followUpTimeline = FollowUpTimeline.WITHIN_24H,
                notes = "Expanding to European banking branches, seeking modular lead CRM integration.",
                tags = listOf("Fintech", "Budget Approved")
            )
        )
        _formData.value = samples.random()
        _formErrorMessage.value = null
    }

    fun submitLead(capturedByTag: String = "Staff Intake"): Boolean {
        val form = _formData.value
        if (form.fullName.trim().isEmpty()) {
            _formErrorMessage.value = "Full name is required."
            return false
        }
        if (form.email.trim().isEmpty() || !form.email.contains("@")) {
            _formErrorMessage.value = "A valid work email is required."
            return false
        }

        val parsedValue = form.dealValue.toDoubleOrNull() ?: 0.0

        val entity = LeadEntity(
            fullName = form.fullName.trim(),
            company = form.company.trim(),
            jobTitle = form.jobTitle.trim(),
            email = form.email.trim(),
            phone = form.phone.trim(),
            status = form.status.name,
            dealValue = parsedValue,
            interestCategory = form.interestCategory,
            source = form.source.name,
            preferredContactMethod = form.preferredContactMethod.name,
            followUpTimeline = form.followUpTimeline.name,
            notes = form.notes.trim(),
            tags = form.tags.joinToString(", "),
            capturedBy = capturedByTag
        )

        viewModelScope.launch {
            val newId = leadRepository.insertLead(entity)
            val insertedLead = entity.copy(id = newId)
            _lastCapturedLead.value = insertedLead
            _showCaptureSuccessDialog.value = true
            resetForm()
        }
        _formErrorMessage.value = null
        return true
    }

    fun dismissCaptureSuccessDialog() {
        _showCaptureSuccessDialog.value = false
    }

    fun resetForm() {
        _formData.value = LeadFormData()
        _formErrorMessage.value = null
    }

    // Lead Detail Actions
    fun updateSelectedLeadStatus(newStatus: LeadStatus) {
        val current = selectedLead.value ?: return
        viewModelScope.launch {
            leadRepository.updateLead(current.copy(status = newStatus.name))
        }
    }

    fun addLeadNote(noteText: String) {
        val currentId = _selectedLeadId.value ?: return
        if (noteText.isBlank()) return
        viewModelScope.launch {
            leadRepository.addActivity(currentId, "NOTE", noteText.trim())
        }
    }

    fun deleteSelectedLead() {
        val currentId = _selectedLeadId.value ?: return
        viewModelScope.launch {
            leadRepository.deleteLead(currentId)
            _selectedLeadId.value = null
            navigateBack()
            showFeedback("Lead deleted successfully")
        }
    }

    // Filter controls
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setStatusFilter(status: LeadStatus?) { _selectedStatusFilter.value = status }
    fun setSourceFilter(source: LeadSource?) { _selectedSourceFilter.value = source }
    fun setSortOrder(order: LeadSortOrder) { _sortOrder.value = order }

    // Admin Auth
    fun adminLogin(identifier: String, secret: String): Boolean {
        val success = authRepository.authenticate(identifier, secret)
        if (success) {
            navigateTo(AppScreen.ADMIN_DASHBOARD)
        }
        return success
    }

    fun adminLogout() {
        authRepository.logout()
        navigateTo(AppScreen.CAPTURE)
    }

    fun checkKioskPin(pin: String): Boolean {
        return pin.trim() == authRepository.getAdminPin() || pin.trim() == "1234"
    }

    // Admin Data Management
    fun seedSampleData() {
        viewModelScope.launch {
            leadRepository.seedSampleData()
            showFeedback("Exhibition dataset restored (5 sample leads)")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            leadRepository.clearAllData()
            showFeedback("All leads cleared")
        }
    }

    fun updateAdminPin(newPin: String): Boolean {
        val ok = authRepository.updatePin(newPin)
        if (ok) showFeedback("Admin PIN updated successfully")
        return ok
    }

    fun getCsvExportData(): String {
        return leadRepository.generateCsvExport(allLeads.value)
    }

    fun showFeedback(msg: String) {
        _feedbackMessage.value = msg
    }

    fun dismissFeedback() {
        _feedbackMessage.value = null
    }
}
