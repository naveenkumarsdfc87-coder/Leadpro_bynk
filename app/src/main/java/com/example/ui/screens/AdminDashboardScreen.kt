package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadStatus
import com.example.ui.components.LeadItemCard
import com.example.ui.components.MetricCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LeadCold
import com.example.ui.theme.LeadConverted
import com.example.ui.theme.LeadHot
import com.example.ui.theme.LeadQualified
import com.example.ui.theme.LeadWarm
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LeadViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: LeadViewModel,
    onNavigateToLeadsList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCapture: () -> Unit,
    onNavigateToKiosk: () -> Unit,
    onOpenLeadDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val leads by viewModel.allLeads.collectAsState()
    val adminName by viewModel.adminName.collectAsState()

    val totalLeads = leads.size
    val hotLeadsCount = leads.count { it.status.equals(LeadStatus.HOT.name, ignoreCase = true) }
    val warmLeadsCount = leads.count { it.status.equals(LeadStatus.WARM.name, ignoreCase = true) }
    val coldLeadsCount = leads.count { it.status.equals(LeadStatus.COLD.name, ignoreCase = true) }
    val qualifiedLeadsCount = leads.count { it.status.equals(LeadStatus.QUALIFIED.name, ignoreCase = true) }
    val convertedLeadsCount = leads.count { it.status.equals(LeadStatus.CONVERTED.name, ignoreCase = true) }

    val totalPipeline = leads.sumOf { it.dealValue }
    val conversionRate = if (totalLeads > 0) (convertedLeadsCount.toFloat() / totalLeads * 100).toInt() else 0

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    currencyFormatter.maximumFractionDigits = 0

    val hotLeads = remember(leads) {
        leads.filter { it.status.equals(LeadStatus.HOT.name, ignoreCase = true) }.take(3)
    }

    fun shareCsv() {
        val csvData = viewModel.getCsvExportData()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, csvData)
            putExtra(Intent.EXTRA_SUBJECT, "LeadCapture_Export_${System.currentTimeMillis()}.csv")
            type = "text/csv"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Leads via")
        context.startActivity(shareIntent)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Admin Executive Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Logged in as $adminName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("admin_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Admin Settings"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.adminLogout() },
                        modifier = Modifier.testTag("admin_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout Admin"
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
            // KPI METRICS 2x2 GRID
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Total Leads",
                        value = totalLeads.toString(),
                        icon = Icons.Default.People,
                        iconColor = PrimaryBlue,
                        subText = "$convertedLeadsCount Converted",
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_total_leads"
                    )
                    MetricCard(
                        title = "Pipeline Value",
                        value = currencyFormatter.format(totalPipeline),
                        icon = Icons.Default.MonetizationOn,
                        iconColor = AmberGold,
                        subText = "Active Deals",
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_pipeline_value"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Hot Opportunities",
                        value = hotLeadsCount.toString(),
                        icon = Icons.Default.LocalFireDepartment,
                        iconColor = LeadHot,
                        subText = "Immediate Priority",
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_hot_leads"
                    )
                    MetricCard(
                        title = "Conversion Rate",
                        value = "$conversionRate%",
                        icon = Icons.Default.ThumbUp,
                        iconColor = EmeraldSuccess,
                        subText = "Won vs Total",
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_conversion_rate"
                    )
                }
            }

            // QUICK ACTIONS ROW
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToLeadsList,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_view_all_leads_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("All Leads ($totalLeads)", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { shareCsv() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_export_csv_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV", fontSize = 13.sp)
                    }
                }
            }

            // SECONDARY ACTIONS (CAPTURE / KIOSK)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToCapture,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_new_capture_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lead Intake", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onNavigateToKiosk,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_kiosk_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kiosk Mode", fontSize = 13.sp)
                    }
                }
            }

            // PIPELINE DISTRIBUTION FUNNEL
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Lead Pipeline Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        FunnelBarItem("🔥 Hot Priority", hotLeadsCount, totalLeads, LeadHot)
                        FunnelBarItem("☀️ Warm Interest", warmLeadsCount, totalLeads, LeadWarm)
                        FunnelBarItem("🎯 Qualified Leads", qualifiedLeadsCount, totalLeads, LeadQualified)
                        FunnelBarItem("🏆 Converted / Won", convertedLeadsCount, totalLeads, LeadConverted)
                        FunnelBarItem("❄️ Cold Inquiries", coldLeadsCount, totalLeads, LeadCold)
                    }
                }
            }

            // HOT LEADS QUEUE
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "High Priority Hot Leads",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToLeadsList)
                            .padding(4.dp)
                    )
                }
            }

            if (hotLeads.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hot priority leads currently queued.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(hotLeads, key = { it.id }) { lead ->
                    LeadItemCard(
                        lead = lead,
                        onClick = { onOpenLeadDetail(lead.id) },
                        onCallClick = {
                            if (lead.phone.isNotBlank()) {
                                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = android.net.Uri.parse("tel:${lead.phone}")
                                }
                                context.startActivity(callIntent)
                            }
                        },
                        onEmailClick = {
                            if (lead.email.isNotBlank()) {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = android.net.Uri.parse("mailto:${lead.email}")
                                    putExtra(Intent.EXTRA_SUBJECT, "Follow up from our meeting")
                                }
                                context.startActivity(emailIntent)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FunnelBarItem(
    label: String,
    count: Int,
    total: Int,
    barColor: Color
) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    val percentage = (progress * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count ($percentage%)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
