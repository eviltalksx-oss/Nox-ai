package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.NoxPreferences
import com.example.ui.chat.ChatScreen
import com.example.ui.code.CodeAssistantScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.device.DeviceIntegrationScreen
import com.example.ui.files.FileUploadScreen
import com.example.ui.memory.MemoryScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.NoxTheme
import com.example.ui.voice.VoiceAssistantScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = NoxPreferences(applicationContext)
        val onboardingCompleted = runBlocking { preferences.onboardingCompletedFlow.first() }
        val displayName = runBlocking { preferences.displayNameFlow.first() }

        setContent {
            NoxTheme {
                val navController = rememberNavController()
                val startDest = if (onboardingCompleted) "dashboard" else "onboarding"

                NavHost(navController = navController, startDestination = startDest) {
                    composable("onboarding") {
                        OnboardingScreen(preferences = preferences) {
                            navController.navigate("dashboard") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    }
                    composable("dashboard") {
                        DashboardScreen(
                            displayName = displayName,
                            onNavigateChat = { navController.navigate("chat") },
                            onNavigateVoice = { navController.navigate("voice") },
                            onNavigateCode = { navController.navigate("code") },
                            onNavigateFiles = { navController.navigate("files") },
                            onNavigateDevice = { navController.navigate("device") },
                            onNavigateMemory = { navController.navigate("memory") },
                            onNavigateSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("chat") {
                        ChatScreen(preferences = preferences) {
                            navController.popBackStack()
                        }
                    }
                    composable("voice") {
                        VoiceAssistantScreen {
                            navController.popBackStack()
                        }
                    }
                    composable("code") {
                        CodeAssistantScreen {
                            navController.popBackStack()
                        }
                    }
                    composable("files") {
                        FileUploadScreen {
                            navController.popBackStack()
                        }
                    }
                    composable("device") {
                        DeviceIntegrationScreen {
                            navController.popBackStack()
                        }
                    }
                    composable("memory") {
                        MemoryScreen {
                            navController.popBackStack()
                        }
                    }
                    composable("settings") {
                        SettingsScreen(preferences = preferences) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}
