package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadEntity
import com.example.data.model.LeadStatus
import com.example.ui.theme.LeadCold
import com.example.ui.theme.LeadColdBg
import com.example.ui.theme.LeadConverted
import com.example.ui.theme.LeadConvertedBg
import com.example.ui.theme.LeadHot
import com.example.ui.theme.LeadHotBg
import com.example.ui.theme.LeadLost
import com.example.ui.theme.LeadLostBg
import com.example.ui.theme.LeadQualified
import com.example.ui.theme.LeadQualifiedBg
import com.example.ui.theme.LeadWarm
import com.example.ui.theme.LeadWarmBg
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeadStatusBadge(statusStr: String, modifier: Modifier = Modifier) {
    val status = LeadStatus.fromString(statusStr)
    val (bgColor, textColor) = when (status) {
        LeadStatus.HOT -> Pair(LeadHotBg, LeadHot)
        LeadStatus.WARM -> Pair(LeadWarmBg, LeadWarm)
        LeadStatus.COLD -> Pair(LeadColdBg, LeadCold)
        LeadStatus.QUALIFIED -> Pair(LeadQualifiedBg, LeadQualified)
        LeadStatus.CONVERTED -> Pair(LeadConvertedBg, LeadConverted)
        LeadStatus.LOST -> Pair(LeadLostBg, LeadLost)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = status.emoji,
                fontSize = 11.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = status.label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    subText: String? = null,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    Card(
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LeadItemCard(
    lead: LeadEntity,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    currencyFormatter.maximumFractionDigits = 0
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("lead_card_${lead.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle with initials
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = lead.fullName
                        .split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .map { it.first().uppercase() }
                        .joinToString("")
                    Text(
                        text = if (initials.isNotEmpty()) initials else "L",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lead.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val companyText = if (lead.company.isNotBlank()) {
                        if (lead.jobTitle.isNotBlank()) "${lead.jobTitle} • ${lead.company}" else lead.company
                    } else if (lead.jobTitle.isNotBlank()) {
                        lead.jobTitle
                    } else {
                        lead.email
                    }
                    Text(
                        text = companyText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                LeadStatusBadge(statusStr = lead.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details row: Value, Source, Date & Direct action icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (lead.dealValue > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = currencyFormatter.format(lead.dealValue),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = lead.interestCategory,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (lead.phone.isNotBlank()) {
                        IconButton(
                            onClick = onCallClick,
                            modifier = Modifier.size(36.dp).testTag("call_lead_${lead.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call lead",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onEmailClick,
                        modifier = Modifier.size(36.dp).testTag("email_lead_${lead.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email lead",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KioskExitPinDialog(
    onDismiss: () -> Unit,
    onConfirmPin: (String) -> Boolean
) {
    var pinText by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Exit Kiosk Mode") },
        text = {
            Column {
                Text(
                    text = "Enter Administrator PIN to return to Admin / Staff controls. (Default demo PIN: 1234)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pinText,
                    onValueChange = {
                        pinText = it
                        hasError = false
                    },
                    label = { Text("Admin PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = hasError,
                    supportingText = if (hasError) {
                        { Text("Incorrect PIN. Please try again.") }
                    } else null,
                    modifier = Modifier.fillMaxWidth().testTag("kiosk_pin_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ok = onConfirmPin(pinText)
                    if (!ok) {
                        hasError = true
                    }
                },
                modifier = Modifier.testTag("kiosk_pin_confirm_btn")
            ) {
                Text("Unlock")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
