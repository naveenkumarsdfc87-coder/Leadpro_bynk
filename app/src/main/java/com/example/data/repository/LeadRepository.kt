package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.LeadDao
import com.example.data.model.LeadActivityEntity
import com.example.data.model.LeadEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LeadRepository(private val leadDao: LeadDao) {

    val allLeads: Flow<List<LeadEntity>> = leadDao.getAllLeads()

    fun getLeadById(id: Long): Flow<LeadEntity?> = leadDao.getLeadById(id)

    suspend fun insertLead(lead: LeadEntity): Long {
        val id = leadDao.insertLead(lead)
        leadDao.insertActivity(
            LeadActivityEntity(
                leadId = id,
                actionType = "CREATED",
                description = "Lead captured via ${lead.source} by ${lead.capturedBy}"
            )
        )
        return id
    }

    suspend fun updateLead(lead: LeadEntity, updateDescription: String? = null) {
        val existing = leadDao.getLeadByIdSync(lead.id)
        leadDao.updateLead(lead.copy(updatedAt = System.currentTimeMillis()))
        if (existing != null && existing.status != lead.status) {
            leadDao.insertActivity(
                LeadActivityEntity(
                    leadId = lead.id,
                    actionType = "STATUS_CHANGE",
                    description = "Status updated from ${existing.status} to ${lead.status}"
                )
            )
        } else if (!updateDescription.isNullOrBlank()) {
            leadDao.insertActivity(
                LeadActivityEntity(
                    leadId = lead.id,
                    actionType = "NOTE",
                    description = updateDescription
                )
            )
        }
    }

    suspend fun deleteLead(id: Long) {
        leadDao.deleteActivitiesForLead(id)
        leadDao.deleteLeadById(id)
    }

    suspend fun addActivity(leadId: Long, actionType: String, description: String) {
        leadDao.insertActivity(
            LeadActivityEntity(
                leadId = leadId,
                actionType = actionType,
                description = description
            )
        )
    }

    fun getActivitiesForLead(leadId: Long): Flow<List<LeadActivityEntity>> =
        leadDao.getActivitiesForLead(leadId)

    suspend fun seedSampleData() {
        leadDao.deleteAllActivities()
        leadDao.deleteAllLeads()
        val samples = AppDatabase.getSampleLeads()
        for (lead in samples) {
            val id = leadDao.insertLead(lead)
            leadDao.insertActivity(
                LeadActivityEntity(
                    leadId = id,
                    timestamp = lead.createdAt,
                    actionType = "CREATED",
                    description = "Lead created at ${lead.source}"
                )
            )
            if (lead.notes.isNotBlank()) {
                leadDao.insertActivity(
                    LeadActivityEntity(
                        leadId = id,
                        timestamp = lead.createdAt + 1800000,
                        actionType = "NOTE",
                        description = "Initial rep notes: ${lead.notes}"
                    )
                )
            }
        }
    }

    suspend fun clearAllData() {
        leadDao.deleteAllActivities()
        leadDao.deleteAllLeads()
    }

    fun generateCsvExport(leads: List<LeadEntity>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("ID,Full Name,Company,Job Title,Email,Phone,Status,Deal Value ($),Category,Source,Preferred Contact,Follow-up,Notes,Tags,Captured By,Created At\n")

        for (lead in leads) {
            sb.append(lead.id).append(",")
            sb.append("\"").append(lead.fullName.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.company.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.jobTitle.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.email.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.phone.replace("\"", "\"\"")).append("\",")
            sb.append(lead.status).append(",")
            sb.append(lead.dealValue).append(",")
            sb.append("\"").append(lead.interestCategory.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.source.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.preferredContactMethod.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.followUpTimeline.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.notes.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.tags.replace("\"", "\"\"")).append("\",")
            sb.append("\"").append(lead.capturedBy.replace("\"", "\"\"")).append("\",")
            sb.append(dateFormat.format(Date(lead.createdAt))).append("\n")
        }
        return sb.toString()
    }
}
