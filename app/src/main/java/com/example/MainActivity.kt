package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CalculatorViewModel

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    Calculator(
        title = "Calculator",
        selectedIcon = Icons.Filled.Scale,
        unselectedIcon = Icons.Outlined.Scale,
        tag = "tab_calculator_btn"
    ),
    History(
        title = "History",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History,
        tag = "tab_history_btn"
    ),
    Settings(
        title = "Developer",
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info,
        tag = "tab_settings_btn"
    )
}

class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                val haptic = LocalHapticFeedback.current
                val context = LocalContext.current

                // Asynchronous side-effect listener for the view-model triggers
                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is CalculatorViewModel.UiEvent.TriggerHaptics -> {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            is CalculatorViewModel.UiEvent.ShowToast -> {
                                android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                // Setup screen-to-screen navigation graph
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(
                            onSplashComplete = {
                                navController.navigate("main") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        MainScreenContainer(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenContainer(viewModel: CalculatorViewModel) {
    var selectedTab by remember { mutableStateOf(AppTab.Calculator) }
    val isDark by viewModel.isDarkMode.collectAsState()
    
    val scaffoldBg = if (isDark) Color(0xFF14151C) else Color(0xFFF3F5F7)
    val bottomBarBg = if (isDark) Color(0xFF20222F) else Color.White
    val selectedIconTint = if (isDark) Color(0xFFD0BCFF) else Color(0xFF6750A4)
    val unselectedIconTint = if (isDark) Color(0xFF94A3B8) else Color(0xFF1C1B1F).copy(alpha = 0.6f)
    val indicatorColor = if (isDark) Color(0xFF4F378B).copy(alpha = 0.5f) else Color(0xFFEADDFF)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = scaffoldBg,
        bottomBar = {
            NavigationBar(
                containerColor = bottomBarBg,
                tonalElevation = 4.dp,
                windowInsets = WindowInsets.navigationBars, // Correctly handles safe areas for gesture pills
                modifier = Modifier.testTag("app_bottom_nav_bar")
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = selectedIconTint,
                            selectedTextColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1C1B1F),
                            indicatorColor = indicatorColor,
                            unselectedIconColor = unselectedIconTint,
                            unselectedTextColor = unselectedIconTint
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                ) // Pads out top (status bar) and bottom navigation accurately
        ) {
            // Keep state alive by utilizing sliding visibility or standard layout triggers
            when (selectedTab) {
                AppTab.Calculator -> CalculatorScreen(viewModel = viewModel)
                AppTab.History -> HistoryScreen(viewModel = viewModel)
                AppTab.Settings -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
