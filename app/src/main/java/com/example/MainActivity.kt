package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLeadsListScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AdminSettingsScreen
import com.example.ui.screens.KioskScreen
import com.example.ui.screens.LeadCaptureScreen
import com.example.ui.screens.LeadDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LeadViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LeadCaptureApp()
            }
        }
    }
}

@Composable
fun LeadCaptureApp(viewModel: LeadViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    // Handle system back navigation across custom screen states
    BackHandler(enabled = currentScreen != AppScreen.CAPTURE) {
        viewModel.navigateBack()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            AppScreen.CAPTURE -> {
                LeadCaptureScreen(
                    viewModel = viewModel,
                    onNavigateToAdmin = {
                        if (isAdminLoggedIn) {
                            viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                        } else {
                            viewModel.navigateTo(AppScreen.ADMIN_LOGIN)
                        }
                    },
                    onNavigateToKiosk = {
                        viewModel.navigateTo(AppScreen.KIOSK)
                    }
                )
            }

            AppScreen.KIOSK -> {
                KioskScreen(
                    viewModel = viewModel,
                    onExitKiosk = {
                        viewModel.navigateTo(AppScreen.CAPTURE)
                    }
                )
            }

            AppScreen.ADMIN_LOGIN -> {
                AdminLoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                    },
                    onBackToCapture = {
                        viewModel.navigateTo(AppScreen.CAPTURE)
                    }
                )
            }

            AppScreen.ADMIN_DASHBOARD -> {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToLeadsList = {
                        viewModel.navigateTo(AppScreen.ADMIN_LEADS_LIST)
                    },
                    onNavigateToSettings = {
                        viewModel.navigateTo(AppScreen.ADMIN_SETTINGS)
                    },
                    onNavigateToCapture = {
                        viewModel.navigateTo(AppScreen.CAPTURE)
                    },
                    onNavigateToKiosk = {
                        viewModel.navigateTo(AppScreen.KIOSK)
                    },
                    onOpenLeadDetail = { leadId ->
                        viewModel.openLeadDetail(leadId)
                    }
                )
            }

            AppScreen.ADMIN_LEADS_LIST -> {
                AdminLeadsListScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        viewModel.navigateBack()
                    },
                    onNavigateToCapture = {
                        viewModel.navigateTo(AppScreen.CAPTURE)
                    },
                    onOpenLeadDetail = { leadId ->
                        viewModel.openLeadDetail(leadId)
                    }
                )
            }

            AppScreen.LEAD_DETAIL -> {
                LeadDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        viewModel.navigateBack()
                    }
                )
            }

            AppScreen.ADMIN_SETTINGS -> {
                AdminSettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        viewModel.navigateBack()
                    }
                )
            }
        }
    }
}
