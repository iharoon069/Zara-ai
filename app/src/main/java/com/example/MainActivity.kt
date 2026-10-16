package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.ZaraBottomNavBar
import com.example.ui.components.ZaraScreen
import com.example.ui.screens.ConversationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PermissionsPrivacyScreen
import com.example.ui.screens.PersonalityScreen
import com.example.ui.screens.PhoneAssistantScreen
import com.example.ui.screens.ScreenAssistScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksAutomationScreen
import com.example.ui.theme.ZaraDeepVoid
import com.example.ui.theme.ZaraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZaraTheme {
                val viewModel: MainViewModel = viewModel()
                val onboardingDone by viewModel.preferences.onboardingCompleted.collectAsState()

                var currentScreen by remember(onboardingDone) {
                    mutableStateOf(if (onboardingDone) ZaraScreen.HOME else ZaraScreen.ONBOARDING)
                }

                val showBottomBar = currentScreen != ZaraScreen.ONBOARDING

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ZaraDeepVoid),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    bottomBar = {
                        if (showBottomBar) {
                            ZaraBottomNavBar(
                                currentScreen = currentScreen,
                                onNavigate = { destination ->
                                    currentScreen = destination
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(ZaraDeepVoid)
                    ) {
                        when (currentScreen) {
                            ZaraScreen.ONBOARDING -> {
                                OnboardingScreen(
                                    viewModel = viewModel,
                                    onComplete = {
                                        currentScreen = ZaraScreen.HOME
                                    }
                                )
                            }
                            ZaraScreen.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigate = { screen -> currentScreen = screen }
                                )
                            }
                            ZaraScreen.CONVERSATION -> {
                                ConversationScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = ZaraScreen.HOME }
                                )
                            }
                            ZaraScreen.ASSISTANT -> {
                                PhoneAssistantScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = ZaraScreen.HOME }
                                )
                            }
                            ZaraScreen.SCREEN_ASSIST -> {
                                ScreenAssistScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = ZaraScreen.HOME }
                                )
                            }
                            ZaraScreen.TASKS -> {
                                TasksAutomationScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = ZaraScreen.HOME }
                                )
                            }
                            ZaraScreen.PERSONALITY -> {
                                PersonalityScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = ZaraScreen.SETTINGS }
                                )
                            }
                            ZaraScreen.MEMORY -> {
                                MemoryScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = ZaraScreen.SETTINGS }
                                )
                            }
                            ZaraScreen.PERMISSIONS -> {
                                PermissionsPrivacyScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = ZaraScreen.SETTINGS }
                                )
                            }
                            ZaraScreen.SETTINGS -> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onNavigate = { screen -> currentScreen = screen },
                                    onBack = { currentScreen = ZaraScreen.HOME }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
