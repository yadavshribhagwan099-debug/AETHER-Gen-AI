package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AetherRoute
import com.example.ui.viewmodel.AetherViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AetherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by viewModel.appTheme.collectAsState()
            LaunchedEffect(appTheme) {
                AetherThemeManager.applyTheme(appTheme)
            }

            MyApplicationTheme {
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val currentRoute by viewModel.currentRoute.collectAsState()
                val apiOverrideKey by viewModel.customApiKey.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isLoggedIn) {
                            AetherGlassNavigationBar(
                                selectedRoute = currentRoute,
                                onRouteSelected = { viewModel.navigateTo(it) },
                                viewModel = viewModel
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AetherSpaceDark)
                    ) {
                        // Drawing starry particle network backdrops
                        NeuralSpaceBackground()

                        // Multi-view layout stream matching navigation paths
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            if (!isLoggedIn) {
                                AetherAuthScreen(viewModel = viewModel)
                            } else {
                                AnimatedContent(
                                    targetState = currentRoute,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                                    },
                                    label = "ScreenTransition"
                                ) { route ->
                                    when (route) {
                                        AetherRoute.DASHBOARD -> DashboardScreen(
                                            viewModel = viewModel,
                                            onRouteSelected = { viewModel.navigateTo(it) }
                                        )
                                        AetherRoute.CHAT -> ChatScreen(viewModel = viewModel)
                                        AetherRoute.CAMERA_VISION -> CameraVisionScreen(viewModel = viewModel)
                                        AetherRoute.PRODUCTIVITY -> ProductivityScreen(viewModel = viewModel)
                                        AetherRoute.NEURAL_WORKSPACE -> NeuralWorkspaceScreen(viewModel = viewModel)
                                        AetherRoute.STUDY_LAB -> StudyLabScreen(viewModel = viewModel)
                                        AetherRoute.MEMORY_MATRIX -> MemoryMatrixScreen(viewModel = viewModel)
                                        AetherRoute.SETTINGS -> SettingsScreen(viewModel = viewModel)
                                    }
                                }
                            }
                        }

                        // Floating warning banner removed as per user preference
                    }
                }
            }
        }
    }
}

@Composable
fun AetherGlassNavigationBar(
    selectedRoute: AetherRoute,
    onRouteSelected: (AetherRoute) -> Unit,
    viewModel: AetherViewModel
) {
    val appLanguage by viewModel.appLanguage.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AetherNebulaBlue.copy(alpha = 0.95f))
            .border(width = 1.dp, color = AetherGlassBorder)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .testTag("glass_navigation_bar"),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            Triple(AetherRoute.DASHBOARD, Icons.Default.Hexagon, "CORE"),
            Triple(AetherRoute.CHAT, Icons.Default.Message, "CHAT"),
            Triple(AetherRoute.CAMERA_VISION, Icons.Default.CameraAlt, "SCAN"),
            Triple(AetherRoute.PRODUCTIVITY, Icons.Default.HourglassEmpty, "CHRONO"),
            Triple(AetherRoute.SETTINGS, Icons.Default.Tune, "CONFIG")
        )

        navItems.forEach { (route, icon, label) ->
            val isActive = selectedRoute == route
            val translatedLabel = com.example.ui.translation.Translator.get(label, appLanguage)
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onRouteSelected(route) }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = translatedLabel,
                    tint = if (isActive) AetherCyberCyan else AetherTextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = translatedLabel,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isActive) AetherCyberCyan else AetherTextMuted,
                    maxLines = 1,
                    letterSpacing = 0.5.sp
                )
                if (isActive) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(2.dp)
                            .background(AetherCyberCyan, shape = RoundedCornerShape(2.dp))
                    )
                } else {
                    Spacer(modifier = Modifier.height(5.dp)) // Maintain layout height alignment
                }
            }
        }
    }
}
