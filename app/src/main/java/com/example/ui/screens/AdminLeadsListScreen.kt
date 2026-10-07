package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadSource
import com.example.data.model.LeadStatus
import com.example.ui.components.LeadItemCard
import com.example.ui.viewmodel.LeadSortOrder
import com.example.ui.viewmodel.LeadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLeadsListScreen(
    viewModel: LeadViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCapture: () -> Unit,
    onOpenLeadDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val leads by viewModel.filteredLeads.collectAsState()
    val allLeads by viewModel.allLeads.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
    val selectedSource by viewModel.selectedSourceFilter.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showSourceFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Leads & Contacts CRM",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${leads.size} of ${allLeads.size} leads",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard"
                        )
                    }
                },
                actions = {
                    // Sort Menu
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.testTag("sort_menu_btn")
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort Leads")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Newest First") },
                                onClick = {
                                    viewModel.setSortOrder(LeadSortOrder.NEWEST_FIRST)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Deal Value (High to Low)") },
                                onClick = {
                                    viewModel.setSortOrder(LeadSortOrder.VALUE_HIGH_TO_LOW)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Name (A to Z)") },
                                onClick = {
                                    viewModel.setSortOrder(LeadSortOrder.NAME_A_TO_Z)
                                    showSortMenu = false
                                }
                            )
                        }
                    }

                    // Source Filter Menu
                    Box {
                        IconButton(
                            onClick = { showSourceFilterMenu = true },
                            modifier = Modifier.testTag("source_filter_menu_btn")
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter by Source")
                        }
                        DropdownMenu(
                            expanded = showSourceFilterMenu,
                            onDismissRequest = { showSourceFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Sources") },
                                onClick = {
                                    viewModel.setSourceFilter(null)
                                    showSourceFilterMenu = false
                                }
                            )
                            LeadSource.entries.forEach { src ->
                                DropdownMenuItem(
                                    text = { Text(src.displayName) },
                                    onClick = {
                                        viewModel.setSourceFilter(src)
                                        showSourceFilterMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCapture,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_lead")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Capture New Lead")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // SEARCH FIELD
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by name, company, email, notes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("leads_search_input")
            )

            // STATUS FILTER CHIPS HORIZONTAL ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { viewModel.setStatusFilter(null) },
                    label = { Text("All (${allLeads.size})") }
                )
                LeadStatus.entries.forEach { status ->
                    val count = allLeads.count { it.status.equals(status.name, ignoreCase = true) }
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = {
                            viewModel.setStatusFilter(if (selectedStatus == status) null else status)
                        },
                        label = { Text("${status.emoji} ${status.label} ($count)") }
                    )
                }
            }

            if (selectedSource != null) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtered by source: ${selectedSource!!.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = { viewModel.setSourceFilter(null) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Remove source filter", modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // LEADS LIST
            if (leads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No Leads Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try adjusting your search terms or filters" else "Tap '+' below to capture your first contact",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(leads, key = { it.id }) { lead ->
                        LeadItemCard(
                            lead = lead,
                            onClick = { onOpenLeadDetail(lead.id) },
                            onCallClick = {
                                if (lead.phone.isNotBlank()) {
                                    val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${lead.phone}")
                                    }
                                    context.startActivity(callIntent)
                                }
                            },
                            onEmailClick = {
                                if (lead.email.isNotBlank()) {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${lead.email}")
                                        putExtra(Intent.EXTRA_SUBJECT, "Follow up from our meeting")
                                    }
                                    context.startActivity(emailIntent)
                                }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                    }
                }
            }
        }
    }
}
