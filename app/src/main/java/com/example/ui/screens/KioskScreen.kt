package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContactMethod
import com.example.data.model.FollowUpTimeline
import com.example.data.model.LeadSource
import com.example.data.model.LeadStatus
import com.example.ui.components.KioskExitPinDialog
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.LeadFormData
import com.example.ui.viewmodel.LeadViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KioskScreen(
    viewModel: LeadViewModel,
    onExitKiosk: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visitorName by remember { mutableStateOf("") }
    var visitorCompany by remember { mutableStateOf("") }
    var visitorEmail by remember { mutableStateOf("") }
    var visitorPhone by remember { mutableStateOf("") }
    var visitorNote by remember { mutableStateOf("") }
    var selectedInterest by remember { mutableStateOf("Enterprise Solutions") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showThankYouDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    val interestOptions = listOf(
        "Enterprise Solutions",
        "AI Automation",
        "Cloud Migration",
        "Custom Consulting",
        "Pricing & Plans"
    )

    fun handleVisitorSubmit() {
        if (visitorName.trim().isEmpty()) {
            errorMessage = "Please enter your name."
            return
        }
        if (visitorEmail.trim().isEmpty() || !visitorEmail.contains("@")) {
            errorMessage = "Please enter a valid email address."
            return
        }

        // Save via ViewModel
        viewModel.updateFullName(visitorName)
        viewModel.updateCompany(visitorCompany)
        viewModel.updateEmail(visitorEmail)
        viewModel.updatePhone(visitorPhone)
        viewModel.updateCategory(selectedInterest)
        viewModel.updateSource(LeadSource.KIOSK)
        viewModel.updateStatus(LeadStatus.WARM)
        viewModel.updateTimeline(FollowUpTimeline.WITHIN_24H)
        viewModel.updateContactMethod(ContactMethod.EMAIL)
        viewModel.updateNotes("Visitor self-registered at kiosk. Notes: $visitorNote")

        viewModel.submitLead(capturedByTag = "Self-Service Kiosk")

        // Reset local form and show thank you
        visitorName = ""
        visitorCompany = ""
        visitorEmail = ""
        visitorPhone = ""
        visitorNote = ""
        errorMessage = null
        showThankYouDialog = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Admin Exit Lock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kiosk Mode",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                IconButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier.testTag("kiosk_exit_lock_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Exit Kiosk (Admin PIN required)",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Welcome to Our Booth!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Drop your details below to receive product resources, schedule a demo, and connect with our specialists.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error display
            AnimatedVisibility(visible = errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Kiosk Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = visitorName,
                        onValueChange = { visitorName = it; errorMessage = null },
                        label = { Text("Your Full Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("kiosk_input_name")
                    )

                    OutlinedTextField(
                        value = visitorCompany,
                        onValueChange = { visitorCompany = it },
                        label = { Text("Company or Organization") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("kiosk_input_company")
                    )

                    OutlinedTextField(
                        value = visitorEmail,
                        onValueChange = { visitorEmail = it; errorMessage = null },
                        label = { Text("Work Email *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("kiosk_input_email")
                    )

                    OutlinedTextField(
                        value = visitorPhone,
                        onValueChange = { visitorPhone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("kiosk_input_phone")
                    )

                    Text(
                        text = "I am interested in:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        interestOptions.forEach { option ->
                            FilterChip(
                                selected = selectedInterest == option,
                                onClick = { selectedInterest = option },
                                label = { Text(option) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = visitorNote,
                        onValueChange = { visitorNote = it },
                        label = { Text("Questions or Comments (Optional)") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("kiosk_input_notes")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { handleVisitorSubmit() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("kiosk_submit_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Submit & Connect",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Visitor Thank You Modal
    if (showThankYouDialog) {
        AlertDialog(
            onDismissRequest = { showThankYouDialog = false },
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
                    Text("Thank You!")
                }
            },
            text = {
                Text(
                    text = "Your contact information has been registered. Our team will reach out with the requested details shortly!",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { showThankYouDialog = false },
                    modifier = Modifier.testTag("kiosk_thankyou_done_btn")
                ) {
                    Text("Register Another Visitor")
                }
            }
        )
    }

    // Exit Kiosk PIN Prompt
    if (showExitDialog) {
        KioskExitPinDialog(
            onDismiss = { showExitDialog = false },
            onConfirmPin = { pin ->
                val isValid = viewModel.checkKioskPin(pin)
                if (isValid) {
                    showExitDialog = false
                    onExitKiosk()
                }
                isValid
            }
        )
    }
}
