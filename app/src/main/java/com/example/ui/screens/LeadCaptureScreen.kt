package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContactMethod
import com.example.data.model.FollowUpTimeline
import com.example.data.model.LeadSource
import com.example.data.model.LeadStatus
import com.example.ui.components.LeadStatusBadge
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LeadViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LeadCaptureScreen(
    viewModel: LeadViewModel,
    onNavigateToAdmin: () -> Unit,
    onNavigateToKiosk: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formData by viewModel.formData.collectAsState()
    val errorMessage by viewModel.formErrorMessage.collectAsState()
    val showSuccessDialog by viewModel.showCaptureSuccessDialog.collectAsState()
    val lastCapturedLead by viewModel.lastCapturedLead.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    var sourceMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Enterprise Solutions",
        "AI Automation",
        "Mobile CRM",
        "Cloud Migration",
        "Hardware & Sensors",
        "Custom Consulting"
    )

    val quickValuePresets = listOf("5000", "15000", "50000", "100000")
    val availableTags = listOf(
        "Decision Maker", "VIP", "Budget Approved", "Urgent", "Technical Evaluator", "Follow-up Demo"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "LeadCapture Pro",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Field & Expo Contact Intake",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Kiosk Mode button
                    IconButton(
                        onClick = onNavigateToKiosk,
                        modifier = Modifier.testTag("kiosk_mode_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Switch to Kiosk Mode",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Admin Portal button
                    Button(
                        onClick = onNavigateToAdmin,
                        colors = if (isAdminLoggedIn) {
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else {
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("admin_portal_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = if (isAdminLoggedIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAdminLoggedIn) "Admin Portal" else "Admin Login",
                            color = if (isAdminLoggedIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Quick pre-fill banner for fast testing
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Quick Lead Intake",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Fill contact info or tap pre-fill for demo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.prefillSampleLead() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("prefill_sample_lead_btn")
                        ) {
                            Text("Fill Demo Lead", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Error Message
            item {
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // SECTION 1: Contact Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "1. Contact Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = formData.fullName,
                            onValueChange = { viewModel.updateFullName(it) },
                            label = { Text("Full Name *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("lead_input_fullname")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = formData.company,
                                onValueChange = { viewModel.updateCompany(it) },
                                label = { Text("Company / Org") },
                                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("lead_input_company")
                            )

                            OutlinedTextField(
                                value = formData.jobTitle,
                                onValueChange = { viewModel.updateJobTitle(it) },
                                label = { Text("Job Title") },
                                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("lead_input_title")
                            )
                        }

                        OutlinedTextField(
                            value = formData.email,
                            onValueChange = { viewModel.updateEmail(it) },
                            label = { Text("Work Email *") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("lead_input_email")
                        )

                        OutlinedTextField(
                            value = formData.phone,
                            onValueChange = { viewModel.updatePhone(it) },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("lead_input_phone")
                        )
                    }
                }
            }

            // SECTION 2: Qualification & Priority
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "2. Lead Priority & Value",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Lead Temperature",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(LeadStatus.HOT, LeadStatus.WARM, LeadStatus.COLD).forEach { status ->
                                val isSelected = formData.status == status
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateStatus(status) },
                                    label = { Text("${status.emoji} ${status.label}") },
                                    modifier = Modifier.weight(1f).testTag("status_chip_${status.name}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Estimated Deal / Budget Value ($)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = formData.dealValue,
                            onValueChange = { viewModel.updateDealValue(it) },
                            label = { Text("Deal Value ($)") },
                            leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("lead_input_deal_value")
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            quickValuePresets.forEach { preset ->
                                val isSelected = formData.dealValue == preset
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.updateDealValue(preset) }
                                ) {
                                    Text(
                                        text = "$$preset",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Category dropdown
                        ExposedDropdownMenuBox(
                            expanded = categoryMenuExpanded,
                            onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
                        ) {
                            OutlinedTextField(
                                value = formData.interestCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Product / Interest Area") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = categoryMenuExpanded,
                                onDismissRequest = { categoryMenuExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            viewModel.updateCategory(cat)
                                            categoryMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 3: Engagement & Follow-up
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "3. Source & Follow-up Timeline",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Lead Source Dropdown
                        ExposedDropdownMenuBox(
                            expanded = sourceMenuExpanded,
                            onExpandedChange = { sourceMenuExpanded = !sourceMenuExpanded }
                        ) {
                            OutlinedTextField(
                                value = formData.source.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Lead Intake Source") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceMenuExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = sourceMenuExpanded,
                                onDismissRequest = { sourceMenuExpanded = false }
                            ) {
                                LeadSource.entries.forEach { src ->
                                    DropdownMenuItem(
                                        text = { Text(src.displayName) },
                                        onClick = {
                                            viewModel.updateSource(src)
                                            sourceMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Preferred Follow-up Method",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ContactMethod.entries.forEach { method ->
                                FilterChip(
                                    selected = formData.preferredContactMethod == method,
                                    onClick = { viewModel.updateContactMethod(method) },
                                    label = { Text(method.displayName) }
                                )
                            }
                        }

                        Text(
                            text = "Follow-up Timeline",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FollowUpTimeline.entries.forEach { timeline ->
                                FilterChip(
                                    selected = formData.followUpTimeline == timeline,
                                    onClick = { viewModel.updateTimeline(timeline) },
                                    label = { Text(timeline.displayName) }
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 4: Tags & Notes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "4. Key Tags & Conversation Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableTags.forEach { tag ->
                                val isSelected = formData.tags.contains(tag)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.toggleTag(tag) },
                                    label = { Text("#$tag") },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else {
                                        { Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = formData.notes,
                            onValueChange = { viewModel.updateNotes(it) },
                            label = { Text("Meeting Takeaways & Discussion Notes") },
                            placeholder = { Text("e.g. Discussed 50 seats plan, requested custom quote...") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth().testTag("lead_input_notes")
                        )
                    }
                }
            }

            // SUBMIT BUTTON
            item {
                Button(
                    onClick = { viewModel.submitLead("Staff Rep") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("capture_lead_submit_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Capture & Save Lead",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Success Dialog on Lead Captured
    if (showSuccessDialog && lastCapturedLead != null) {
        val lead = lastCapturedLead!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissCaptureSuccessDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Lead Captured!")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${lead.fullName} from ${if (lead.company.isNotBlank()) lead.company else "Independent"} has been saved to the database.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LeadStatusBadge(statusStr = lead.status)
                        if (lead.dealValue > 0) {
                            Text(
                                text = "Est. Value: $${lead.dealValue.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissCaptureSuccessDialog() },
                    modifier = Modifier.testTag("dialog_capture_next_btn")
                ) {
                    Text("Capture Next Lead")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.dismissCaptureSuccessDialog()
                        viewModel.openLeadDetail(lead.id)
                    },
                    modifier = Modifier.testTag("dialog_view_lead_btn")
                ) {
                    Text("View Dossier")
                }
            }
        )
    }
}
