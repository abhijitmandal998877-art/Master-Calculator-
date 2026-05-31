package com.example

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CalculatorViewModel
import java.util.Locale

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

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private val viewModel: CalculatorViewModel by viewModels()
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Text-To-Speech for voice results announcement
        tts = TextToSpeech(this, this)

        // Setup notification channel and request permissions on Android 13+
        createNotificationChannel()
        requestNotificationPermission()

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
                            is CalculatorViewModel.UiEvent.SpeakAndNotify -> {
                                // Sound system click: play distinct calculation success tone
                                playCalculateSound()
                                // Speech voice modes: announce the numeric results out loud
                                speakResult(event.speechText)
                                // Notify: show calculation success heads-up details
                                showNotification(event.notificationText)
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                ttsReady = true
            }
        }
    }

    private fun speakResult(text: String) {
        if (ttsReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CalcTTSID")
        }
    }

    private fun playCalculateSound() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Calculator Results"
            val descriptionText = "Notifications showing calculation results"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("calculator_results", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun showNotification(message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "calculator_results")
            .setSmallIcon(applicationInfo.icon)
            .setContentTitle("Calculation Successful")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(777, builder.build())
                }
            } else {
                notificationManager.notify(777, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
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
