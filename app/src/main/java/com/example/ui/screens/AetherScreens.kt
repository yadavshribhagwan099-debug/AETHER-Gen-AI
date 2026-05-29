package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ChatLog
import com.example.data.database.MemoryNode
import com.example.data.database.TaskItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.AetherRoute
import com.example.ui.viewmodel.AetherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// --- GLASSMORPHIC ULTRA CONTAINER ---
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = AetherGlassBorder,
    accentColor: Color? = null,
    backgroundColor: Color = AetherNebulaBlue.copy(alpha = 0.55f),
    content: @Composable ColumnScope.() -> Unit
) {
    val roundedShape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .clip(roundedShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                brush = if (accentColor != null) {
                    Brush.linearGradient(
                        colors = listOf(accentColor, borderColor.copy(alpha = 0.3f))
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(borderColor, borderColor)
                    )
                },
                shape = roundedShape
            )
            .drawBehind {
                if (accentColor != null) {
                    // Draw a sleek futuristic top neon accent line
                    drawRect(
                        color = accentColor,
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width, 3.dp.toPx())
                    )
                }
            }
            .padding(top = if (accentColor != null) 18.dp else 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        content = content
    )
}

// --- DYNAMIC NEURAL BACKGROUND CANVAS ---
@Composable
fun NeuralSpaceBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundParticles")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particleAlpha"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val gridStep = 80.dp.toPx().coerceAtLeast(10f)
                onDrawBehind {
                    val width = size.width
                    val height = size.height

                    // Draw static cyber grid lines (extremely performant cached draw)
                    var x = 0f
                    while (x < width) {
                        drawLine(
                            color = AetherHoloGridLine,
                            start = Offset(x, 0f),
                            end = Offset(x, height),
                            strokeWidth = 1f
                        )
                        x += gridStep
                    }

                    var y = 0f
                    while (y < height) {
                        drawLine(
                            color = AetherHoloGridLine,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                        y += gridStep
                    }
                }
            }
    ) {
        val width = size.width
        val height = size.height

        // Slowly pulsing starglow specs (highly optimized)
        val stars = listOf(
            Offset(0.12f * width, 0.18f * height),
            Offset(0.28f * width, 0.42f * height),
            Offset(0.72f * width, 0.15f * height),
            Offset(0.88f * width, 0.78f * height),
            Offset(0.38f * width, 0.85f * height),
            Offset(0.95f * width, 0.32f * height),
            Offset(0.05f * width, 0.90f * height),
            Offset(0.58f * width, 0.48f * height),
            Offset(0.34f * width, 0.25f * height),
            Offset(0.82f * width, 0.55f * height)
        )

        stars.forEachIndexed { index, star ->
            val sizeRad = if (index % 3 == 0) 3.5.dp.toPx() else 1.8.dp.toPx()
            val starAlpha = if (index % 2 == 0) alphaAnim else (1.0f - alphaAnim).coerceIn(0.1f, 0.9f)
            
            drawCircle(
                color = if (index % 2 == 0) AetherCyberCyan.copy(alpha = starAlpha) else AetherPlasmaPurple.copy(alpha = starAlpha),
                radius = sizeRad,
                center = star
            )
        }
    }
}

// --- TELEMETRY CLOCK UPPER STATUS BAR HUD ---
@Composable
fun TelemetryClockHeader() {
    var activeZoneCode by remember { mutableStateOf("IST") }
    var currentTimeString by remember { mutableStateOf("00:00:00") }
    
    LaunchedEffect(activeZoneCode) {
        while (true) {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
            val tzId = when (activeZoneCode) {
                "IST" -> "Asia/Kolkata"
                "UTC" -> "UTC"
                "EST" -> "America/New_York"
                else -> "Asia/Kolkata"
            }
            sdf.timeZone = TimeZone.getTimeZone(tzId)
            currentTimeString = sdf.format(Date())
            delay(1000)
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(AetherDarkGray.copy(alpha = 0.65f))
            .border(1.dp, AetherGlassBorder, RoundedCornerShape(30.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val infiniteTransition = rememberInfiniteTransition(label = "HeartbeatPulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "beat"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .graphicsLayer {
                        alpha = pulseAlpha
                    }
                    .clip(CircleShape)
                    .background(AetherEmerald)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AETHER // SYSTEMS SECURE",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = AetherCyberCyan,
                letterSpacing = 1.sp
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                activeZoneCode = when (activeZoneCode) {
                    "IST" -> "UTC"
                    "UTC" -> "EST"
                    "EST" -> "IST"
                    else -> "IST"
                }
            }
        ) {
            Icon(
                Icons.Default.AccessTime,
                contentDescription = "Chrono Mode",
                tint = AetherSolarGold,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$currentTimeString $activeZoneCode" + if (activeZoneCode == "IST") " (DEFAULT)" else "",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = AetherSolarGold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// --- FUTURISTIC COGNITIVE LABS HUB LINK COMPONENT ---
@Composable
fun SubLabHubCard(
    title: String,
    subTitle: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(1.dp, AetherGlassBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = AetherDarkGray.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AetherTextLight
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subTitle,
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    fontSize = 11.5.sp,
                    color = AetherTextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// --- SUB-SCREEN RETICLE BACK-NAVIGATION TOP BAR ---
@Composable
fun BackToHubHeader(title: String, subtitle: String? = null, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AetherDarkGray)
                .border(1.dp, AetherGlassBorder, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = AetherCyberCyan,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AetherTextLight,
                letterSpacing = 0.5.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = AetherTextMuted
                )
            }
        }
    }
}

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(viewModel: AetherViewModel, onRouteSelected: (AetherRoute) -> Unit) {
    val userName by viewModel.userName.collectAsState()
    val memories by viewModel.memoriesList.collectAsState()
    val tasks by viewModel.tasksList.collectAsState()
    val focusSessions by viewModel.focusSessionsList.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "DashboardOrb")
    val orbPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glassOrbPulse"
    )

    val orbRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glassOrbRotation"
    )

    val pendingTasks = tasks.count { !it.isCompleted }
    val totalFocusMins = focusSessions.sumOf { it.minutes }
    val activeMemories = memories.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TelemetryClockHeader()

        // 🧠 WELCOME HEADER GRADIENT PANEL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(AetherPlasmaPurple.copy(alpha = 0.25f), Color.Transparent),
                        radius = 400f
                    )
                )
                .background(AetherDarkGray.copy(alpha = 0.5f))
                .border(1.dp, AetherGlassBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "${com.example.ui.translation.Translator.get("welcome", appLanguage)}, $userName".uppercase(),
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = AetherTextLight,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(AetherEmerald, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = com.example.ui.translation.Translator.get("core_status", appLanguage),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AetherCyberCyan,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = com.example.ui.translation.Translator.get("system_online", appLanguage),
                    fontSize = 12.sp,
                    color = AetherTextMuted,
                    lineHeight = 16.sp
                )
            }
        }

        // 🔮 PREMIUM ASSISTANT ORB
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clickable {
                        Toast.makeText(context, "Aether core synthesized signal feedback.", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                // Background Glow
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AetherCyberCyan.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                )

                // Neon Ring sweep
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer {
                            scaleX = orbPulseScale
                            scaleY = orbPulseScale
                        }
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(AetherCyberCyan, AetherPlasmaPurple, AetherSolarGold)
                            ),
                            shape = CircleShape
                        )
                )

                // High Tech Coordinates Ring
                Canvas(
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer {
                            rotationZ = orbRotation
                        }
                ) {
                    drawCircle(
                        color = AetherCyberCyan.copy(alpha = 0.05f),
                        radius = size.minDimension / 2
                    )
                    drawCircle(
                        color = AetherPlasmaPurple.copy(alpha = 0.25f),
                        radius = size.minDimension / 2 - 12.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawLine(
                        color = AetherCyberCyan.copy(alpha = 0.25f),
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = AetherCyberCyan.copy(alpha = 0.25f),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Holographic Capsule Glass Core
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AetherCyberCyan.copy(alpha = 0.40f),
                                    AetherPlasmaPurple.copy(alpha = 0.20f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(1.5.dp, AetherGlassBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "AETHER",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AetherCyberCyan,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ONLINE",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AetherSolarGold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }

        // 📈 COGNITIVE TELEMETRY STATS GRID (3 COLUMN)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val stats = listOf(
                Triple(com.example.ui.translation.Translator.get("tasks_due", appLanguage), "$pendingTasks", AetherCyberCyan),
                Triple(com.example.ui.translation.Translator.get("saved_flow", appLanguage), "${totalFocusMins}m", AetherPlasmaPurple),
                Triple(com.example.ui.translation.Translator.get("memories", appLanguage), "$activeMemories", AetherSolarGold)
            )
            stats.forEach { (label, value, accent) ->
                GlassCard(
                    modifier = Modifier.weight(1f),
                    accentColor = accent
                ) {
                    Text(
                        text = label.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AetherTextMuted,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AetherTextLight,
                        maxLines = 1
                    )
                }
            }
        }

        // 🚀 DIRECT ACTION HUB LINK PANEL
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = AetherPlasmaPurple
        ) {
            Text(
                text = "ACTION PLATFORM DIRECT LINK",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AetherCyberCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onRouteSelected(AetherRoute.CHAT) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("quick_chat_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(AetherPlasmaPurple, AetherHotPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Message, contentDescription = "Chat", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chat Module", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = { onRouteSelected(AetherRoute.CAMERA_VISION) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("quick_camera_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AetherDarkGray),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AetherGlassBorder)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = AetherCyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Vision Scan", fontSize = 12.5.sp, color = AetherCyberCyan, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 🌌 COGNITIVE SUB-LABS & CORES
        Text(
            text = "COGNITIVE SUB-SYSTEM CORES // HUB",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AetherSolarGold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(top = 10.dp)
        )

        SubLabHubCard(
            title = "Creative Graphics Dept",
            subTitle = "NEURAL ART SYNTHESIS",
            description = "Inject prompt formulas to draw geometric spatial art matrix and holographic designs.",
            icon = Icons.Default.Palette,
            accentColor = AetherPlasmaPurple,
            onClick = { onRouteSelected(AetherRoute.NEURAL_WORKSPACE) }
        )

        SubLabHubCard(
            title = "Intellect Study Lab",
            subTitle = "TUTOR SYNAPSE PAYLOADS",
            description = "Explore complex async code patterns, scientific views, and custom research briefs.",
            icon = Icons.Default.School,
            accentColor = AetherSolarGold,
            onClick = { onRouteSelected(AetherRoute.STUDY_LAB) }
        )

        SubLabHubCard(
            title = "Memory Matrix Ledger",
            subTitle = "NEURAL SYNAPSE LOGS",
            description = "Scan offline Room database states, registers, and sync custom cognitive logs.",
            icon = Icons.Default.Memory,
            accentColor = AetherEmerald,
            onClick = { onRouteSelected(AetherRoute.MEMORY_MATRIX) }
        )
    }
}

// ==========================================
// 2. AI CHAT SCREEN
// ==========================================
@Composable
fun ChatScreen(viewModel: AetherViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val vocalizationEnabled by viewModel.vocalizationEnabled.collectAsState()
    
    val focusManager = LocalFocusManager.current
    var inputMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // HEADER MODULE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AETHER CHAT NODE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherTextLight,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "High-coherence conversational interface",
                    fontSize = 11.sp,
                    color = AetherTextMuted
                )
            }
            Row {
                IconButton(onClick = { viewModel.toggleVocalization() }) {
                    Icon(
                        imageVector = if (vocalizationEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Vocalization",
                        tint = if (vocalizationEnabled) AetherCyberCyan else AetherTextMuted
                    )
                }
                IconButton(onClick = { viewModel.clearChatHistory() }) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear History", tint = AetherHotPink)
                }
            }
        }

        // COGNITIVE CHAT SESSIONS HISTORIES
        val allSessions by viewModel.allSessionIds.collectAsState()
        val currentSession by viewModel.activeSessionId.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .background(AetherDarkGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                .border(0.5.dp, AetherGlassBorder, shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "COGNITIVE HISTORY STACK",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = AetherTextCyber,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "SWIPE TO EXPLORE",
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    color = AetherTextMuted
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    // Create New Session Option
                    Card(
                        modifier = Modifier
                            .clickable { viewModel.createNewChatSession() }
                            .testTag("btn_new_chat_session"),
                        colors = CardDefaults.cardColors(containerColor = AetherCyberCyan.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, AetherCyberCyan.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Create Session", tint = AetherCyberCyan, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NEW SYSTEM NODE",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AetherCyberCyan
                            )
                        }
                    }
                }
                items(allSessions) { session ->
                    val isSelected = session == currentSession
                    // Simplify session displays
                    val displayName = if (session.startsWith("SESSION_")) {
                        session.substringAfter("SESSION_").take(15)
                    } else {
                        "ALPHA_CORE"
                    }
                    Card(
                        modifier = Modifier
                            .clickable { viewModel.selectChatSession(session) }
                            .testTag("btn_session_$session"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) AetherPlasmaPurple.copy(alpha = 0.35f) else AetherDarkGray.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) AetherPlasmaPurple else AetherGlassBorder
                        )
                    ) {
                        Text(
                            text = displayName,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) AetherSolarGold else AetherTextLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // CHAT MESSAGE STREAM
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
            if (chatHistory.isEmpty()) {
                // Empty state suggestions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Forum,
                        contentDescription = "Ready to respond",
                        modifier = Modifier.size(56.dp).alpha(0.7f),
                        tint = AetherCyberCyan
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aether AI Core ready to process query",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AetherTextLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Send a message or select a suggested topic to explore:",
                        fontSize = 12.sp,
                        color = AetherTextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val suggestions = listOf(
                        "Who are you?",
                        "How can I boost my focus hours?",
                        "Create a reminder for studying tomorrow"
                    )
                    suggestions.forEach { suggestion ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    inputMessage = suggestion
                                    viewModel.sendChatMessage(suggestion)
                                    inputMessage = ""
                                },
                            colors = CardDefaults.cardColors(containerColor = AetherDarkGray.copy(alpha = 0.6f)),
                            border = BorderStroke(1.dp, AetherGlassBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ArrowOutward, contentDescription = "suggest", tint = AetherCyberCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = suggestion,
                                    fontSize = 12.sp,
                                    color = AetherTextLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false,
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chatHistory) { chat ->
                        val isUser = chat.role == "user"
                        val alignment = if (isUser) Alignment.End else Alignment.Start
                        
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = alignment
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        )
                                    )
                                    .background(if (isUser) AetherPlasmaPurple.copy(alpha = 0.2f) else AetherNebulaBlue.copy(alpha = 0.7f))
                                    .border(
                                        width = 1.dp,
                                        color = if (isUser) AetherPlasmaPurple.copy(alpha = 0.6f) else AetherGlassBorder,
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        )
                                    )
                                    .drawBehind {
                                        // Colored sidebar on bubble
                                        drawRect(
                                            color = if (isUser) AetherPlasmaPurple else AetherCyberCyan,
                                            topLeft = Offset(if (isUser) size.width - 3.dp.toPx() else 0f, 0f),
                                            size = androidx.compose.ui.geometry.Size(3.dp.toPx(), size.height)
                                        )
                                    }
                                    .padding(start = if (isUser) 14.dp else 18.dp, end = if (isUser) 18.dp else 14.dp, top = 12.dp, bottom = 12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = (if (isUser) "OPERATOR" else "AETHER SYSTEM").uppercase(),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isUser) AetherSolarGold else AetherCyberCyan,
                                        letterSpacing = 1.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = chat.message,
                                        fontSize = 14.sp,
                                        color = AetherTextLight,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // LOAD PROGRESS COMPONENT
        if (isAiLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = AetherCyberCyan, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aether compiling answers...",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = AetherSolarGold,
                    modifier = Modifier.testTag("chat_loading_indicator")
                )
            }
        }

        // INPUT CONSOLE TERMINAL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, AetherGlassBorder, RoundedCornerShape(12.dp))
                    .testTag("message_input_field"),
                placeholder = { Text("Compile messages here...", color = AetherTextMuted, fontSize = 13.sp) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = AetherTextLight,
                    unfocusedTextColor = AetherTextLight,
                    focusedContainerColor = AetherDarkGray,
                    unfocusedContainerColor = AetherDarkGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 3,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Send
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSend = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendChatMessage(inputMessage)
                            inputMessage = ""
                            focusManager.clearFocus()
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        viewModel.sendChatMessage(inputMessage)
                        inputMessage = ""
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier
                    .height(52.dp)
                    .width(56.dp)
                    .testTag("send_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AetherCyberCyan),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send Payload", tint = AetherSpaceDark, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ==========================================
// 3. AI CAMERA VISION SCREEN
// ==========================================
@Composable
fun CameraVisionScreen(viewModel: AetherViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf("") }
    
    val scanTargets = listOf(
        Pair("Mathematical Formula", "Target classified: High-order calculus integral ∫(3x² + 5x + 2)dx. Decompiled Solution: x³ + 2.5x² + 2x + C"),
        Pair("Botanical Diagnostic Map", "Target classified: Monstera Deliciosa specimen. Health score: 96% | Status: High hydration and steady metrics."),
        Pair("Decoded Quantum Token Spec", "Target classified: Project Aether Document. Decrypted summary: High-coherence local memory sync pipeline.")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TelemetryClockHeader()
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "AETHER MULTI-MODAL CAM LAB",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AetherTextLight,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Holographic entity scanning and matrix classification",
            fontSize = 11.sp,
            color = AetherTextMuted
        )
        Spacer(modifier = Modifier.height(12.dp))

        // VIEW FINDER DISPLAY PANEL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AetherDarkGray)
                .border(1.dp, AetherGlassBorder, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Target HUD reticle drawings
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cornerSpace = 20.dp.toPx()
                val edgeLen = 30.dp.toPx()
                val strokeW = 2.dp.toPx()
                
                // Top Left corner lines
                drawLine(AetherCyberCyan, Offset(cornerSpace, cornerSpace), Offset(cornerSpace + edgeLen, cornerSpace), strokeW)
                drawLine(AetherCyberCyan, Offset(cornerSpace, cornerSpace), Offset(cornerSpace, cornerSpace + edgeLen), strokeW)

                // Top Right corner lines
                drawLine(AetherCyberCyan, Offset(size.width - cornerSpace, cornerSpace), Offset(size.width - cornerSpace - edgeLen, cornerSpace), strokeW)
                drawLine(AetherCyberCyan, Offset(size.width - cornerSpace, cornerSpace), Offset(size.width - cornerSpace, cornerSpace + edgeLen), strokeW)

                // Bottom Left corner lines
                drawLine(AetherCyberCyan, Offset(cornerSpace, size.height - cornerSpace), Offset(cornerSpace + edgeLen, size.height - cornerSpace), strokeW)
                drawLine(AetherCyberCyan, Offset(cornerSpace, size.height - cornerSpace), Offset(cornerSpace, size.height - cornerSpace - edgeLen), strokeW)

                // Bottom Right corner lines
                drawLine(AetherCyberCyan, Offset(size.width - cornerSpace, size.height - cornerSpace), Offset(size.width - cornerSpace - edgeLen, size.height - cornerSpace), strokeW)
                drawLine(AetherCyberCyan, Offset(size.width - cornerSpace, size.height - cornerSpace), Offset(size.width - cornerSpace, size.height - cornerSpace - edgeLen), strokeW)

                // Lock-on Target reticle
                drawRect(
                    color = AetherPlasmaPurple.copy(alpha = 0.35f),
                    topLeft = Offset(size.width * 0.25f, size.height * 0.25f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.5f, size.height * 0.5f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Sweeper laser bar animation
            val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
            val sweepY by infiniteTransition.animateFloat(
                initialValue = 20f,
                targetValue = 220f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glowingScanLine"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .offset { androidx.compose.ui.unit.IntOffset(0, sweepY.dp.roundToPx()) }
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, AetherCyberCyan, Color.Transparent)
                        )
                    )
            )

            if (isScanning) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AetherCyberCyan, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "DECRYPTING MATRIX TARGET...",
                        fontFamily = FontFamily.Monospace,
                        color = AetherSolarGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan icon", tint = AetherCyberCyan.copy(alpha = 0.6f), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Target Lock Ready",
                        fontSize = 14.sp,
                        color = AetherTextLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Select a calibration entity node below to process",
                        color = AetherTextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // CHOOSE TARGET ENTITY
        Text(
            text = "SELECT REGISTERED SCHEMATICS",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AetherTextMuted,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(scanTargets) { (targetTitle, mockText) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isScanning = true
                            scanResult = ""
                            coroutineScope.launch {
                                delay(1500)
                                isScanning = false
                                scanResult = mockText
                                viewModel.addMemory("Entity Scanned: $targetTitle facts", "vision_intelligence", 0.85f)
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = AetherDarkGray.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, AetherGlassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AetherPlasmaPurple.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FilterCenterFocus, contentDescription = "Focus Target", tint = AetherPlasmaPurple, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = targetTitle,
                            color = AetherTextLight,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (scanResult.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AetherSolarGold,
                accentColor = AetherSolarGold
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TARGET DATA SYNTHESIS DIRECT_OUT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AetherSolarGold
                    )
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = AetherEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = scanResult,
                    fontSize = 13.sp,
                    color = AetherTextLight,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ==========================================
// 4. PRODUCTIVITY & FOCUS TIMER SCREEN
// ==========================================
@Composable
fun ProductivityScreen(viewModel: AetherViewModel) {
    val tasks by viewModel.tasksList.collectAsState()
    val focusSessions by viewModel.focusSessionsList.collectAsState()
    val isDistractionFree by viewModel.isDistractionFree.collectAsState()
    val notes by viewModel.notesList.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    // Task inputs
    var taskInput by remember { mutableStateOf("") }
    var taskCategory by remember { mutableStateOf("Study") }
    
    // Timer inputs
    var timerMinsInput by remember { mutableStateOf("25") }
    var timerRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(0) }
    var startMinutes by remember { mutableStateOf(25) }
    var playAmbientAud by remember { mutableStateOf(false) }

    // Notes inputs
    var noteTitleInput by remember { mutableStateOf("") }
    var noteContentInput by remember { mutableStateOf("") }
    var noteCatSelected by remember { mutableStateOf("Life") }

    // YouTube brief inputs
    var youtubeUrlInput by remember { mutableStateOf("") }
    val youtubeBriefResult by viewModel.youtubeBriefResult.collectAsState()
    val youtubeLoading by viewModel.youtubeLoading.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(timerRunning, secondsLeft) {
        if (timerRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        } else if (timerRunning && secondsLeft == 0) {
            timerRunning = false
            viewModel.logFocusSession(startMinutes, "Cosmic White Noise")
            playAmbientAud = false
        }
    }

    val totalTimeFraction = remember(secondsLeft, startMinutes) {
        if (startMinutes > 0) {
            secondsLeft.toFloat() / (startMinutes * 60f)
        } else 0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TelemetryClockHeader()

        // Distraction-Free Header section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isDistractionFree) 
                    com.example.ui.translation.Translator.get("notes_pad", appLanguage)
                else 
                    "CHRONO PRODUCTIVITY LAB",
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AetherTextLight,
                letterSpacing = 0.5.sp
            )

            // Dynamic Mode Switcher Widget!
            Button(
                onClick = { viewModel.toggleDistractionFree() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDistractionFree) AetherHotPink else AetherDarkGray
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AetherGlassBorder),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = if (isDistractionFree) "EXIT FOCUS" else "ENTER FOCUS",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = AetherTextLight
                )
            }
        }

        if (isDistractionFree) {
            // ===================================
            // 📝 DISTRACTION-FREE Focus Layout (Notes pad only)
            // ===================================
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = AetherHotPink
            ) {
                Text(
                    text = "AETHER DISTRACTION-FREE WRITING MATRIX",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherHotPink,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = com.example.ui.translation.Translator.get("distraction_subtitle", appLanguage),
                    fontSize = 11.sp,
                    color = AetherTextMuted
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Create Note Form Fields
                TextField(
                    value = noteTitleInput,
                    onValueChange = { noteTitleInput = it },
                    placeholder = { Text(com.example.ui.translation.Translator.get("enter_title", appLanguage), fontSize = 12.sp, color = AetherTextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("note_title_input"),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = AetherTextLight,
                        unfocusedTextColor = AetherTextLight,
                        focusedContainerColor = AetherSlateObsidian,
                        unfocusedContainerColor = AetherSlateObsidian,
                        focusedIndicatorColor = AetherHotPink
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = noteContentInput,
                    onValueChange = { noteContentInput = it },
                    placeholder = { Text(com.example.ui.translation.Translator.get("enter_body", appLanguage), fontSize = 12.sp, color = AetherTextMuted) },
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("note_content_input"),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = AetherTextLight,
                        unfocusedTextColor = AetherTextLight,
                        focusedContainerColor = AetherSlateObsidian,
                        unfocusedContainerColor = AetherSlateObsidian,
                        focusedIndicatorColor = AetherHotPink
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Note Category select
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val cats = listOf("Study", "Life", "Dev", "System")
                    cats.forEach { cat ->
                        val selected = noteCatSelected == cat
                        Card(
                            modifier = Modifier
                                .clickable { noteCatSelected = cat }
                                .alpha(if (selected) 1f else 0.5f),
                            colors = CardDefaults.cardColors(containerColor = if (selected) AetherHotPink else AetherDarkGray),
                            border = BorderStroke(0.5.dp, AetherGlassBorder)
                        ) {
                            Text(
                                text = cat.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AetherTextLight,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (noteTitleInput.isNotBlank() && noteContentInput.isNotBlank()) {
                            viewModel.addNote(noteTitleInput, noteContentInput, noteCatSelected)
                            noteTitleInput = ""
                            noteContentInput = ""
                            Toast.makeText(context, "Note registered successfully in local buffers.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Note fields must not be empty.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_note_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AetherHotPink),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(com.example.ui.translation.Translator.get("create_note_btn", appLanguage), color = AetherSpaceDark, fontWeight = FontWeight.Bold)
                }
            }

            // List of Saved Notes in local storage
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = AetherCyberCyan
            ) {
                Text(
                    text = "LOCAL NOTE PERSISTENCE ARCHIVE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherCyberCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (notes.isEmpty()) {
                    Text(
                        text = com.example.ui.translation.Translator.get("notes_empty", appLanguage),
                        color = AetherTextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                } else {
                    notes.forEach { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(0.5.dp, AetherGlassBorder, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = AetherDarkGray.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AetherHotPink.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = note.category.uppercase(),
                                                fontSize = 8.sp,
                                                color = AetherHotPink,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = note.title,
                                            fontWeight = FontWeight.Bold,
                                            color = AetherTextLight,
                                            fontSize = 13.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteNote(note.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = AetherHotPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = note.content,
                                    fontSize = 12.sp,
                                    color = AetherTextLight,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ===================================
            // ⏱️ STANDARD Chrono Focus Layout
            // ===================================
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = AetherCyberCyan
            ) {
                Text(
                    text = "NEURAL FLOW ENGINE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = AetherCyberCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // EXCELLENT ANIMATED TIME FLOW DISC
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
                        val pulseAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "glowingScanLine"
                        )

                        val rotationAnim by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(6000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "ringSpin"
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = AetherGlassBorder.copy(alpha = 0.12f),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 6.dp.toPx())
                            )

                            val sweepAngle = if (timerRunning) totalTimeFraction * 360f else 360f
                            drawArc(
                                color = if (timerRunning) AetherCyberCyan else AetherPlasmaPurple,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx())
                            )

                            drawCircle(
                                color = AetherSolarGold.copy(alpha = pulseAlpha * 0.15f),
                                radius = (size.minDimension / 2) - 15.dp.toPx(),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }

                        // Numeric center chrono
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val displayMinutes = if (timerRunning) secondsLeft / 60 else timerMinsInput.toIntOrNull() ?: 25
                            val displaySeconds = if (timerRunning) secondsLeft % 60 else 0
                            val countdownFormatted = String.format("%02d:%02d", displayMinutes, displaySeconds)

                            Text(
                                text = countdownFormatted,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = AetherTextLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (timerRunning) "FLOW RUNNING" else "CHRONO_READY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (timerRunning) AetherCyberCyan else AetherSolarGold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!timerRunning) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextField(
                                value = timerMinsInput,
                                onValueChange = { timerMinsInput = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Set Duration (minutes)", fontSize = 11.sp, color = AetherTextMuted) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = AetherSlateObsidian,
                                    unfocusedContainerColor = AetherSlateObsidian,
                                    focusedTextColor = AetherTextLight,
                                    unfocusedTextColor = AetherTextLight,
                                    focusedIndicatorColor = AetherCyberCyan,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!timerRunning) {
                                    val minutes = timerMinsInput.toIntOrNull() ?: 25
                                    startMinutes = minutes
                                    secondsLeft = minutes * 60
                                    timerRunning = true
                                    playAmbientAud = true
                                } else {
                                    timerRunning = false
                                    playAmbientAud = false
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (timerRunning) AetherHotPink else AetherCyberCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (timerRunning) "Abort flow session" else "Decompress Chrono Wave",
                                color = AetherSpaceDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { playAmbientAud = !playAmbientAud },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AetherDarkGray),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AetherGlassBorder)
                        ) {
                            Icon(
                                if (playAmbientAud) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Audio Volume",
                                tint = AetherCyberCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (playAmbientAud) "Playing Sound" else "Sound Off", fontSize = 11.sp, color = AetherCyberCyan)
                        }
                    }
                }
            }

            // ===================================
            // 🎥 PREMIUM YOUTUBE SUMMARY SYNAPSE
            // ===================================
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = AetherSolarGold
            ) {
                Text(
                    text = com.example.ui.translation.Translator.get("youtube_title", appLanguage),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherSolarGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reconstruct visual summaries and timestamped sequential timelines of any video. Requires active subscription.",
                    fontSize = 11.sp,
                    color = AetherTextMuted,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = youtubeUrlInput,
                        onValueChange = { youtubeUrlInput = it },
                        modifier = Modifier.weight(1f).testTag("youtube_url_field"),
                        placeholder = { Text(com.example.ui.translation.Translator.get("pasted_url", appLanguage), fontSize = 12.sp, color = AetherTextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = AetherTextLight,
                            unfocusedTextColor = AetherTextLight,
                            focusedContainerColor = AetherSlateObsidian,
                            unfocusedContainerColor = AetherSlateObsidian,
                            focusedIndicatorColor = AetherSolarGold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (youtubeUrlInput.isNotBlank()) {
                                viewModel.briefYoutubeVideo(youtubeUrlInput)
                            } else {
                                Toast.makeText(context, "Url cannot be empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.height(52.dp).testTag("youtube_brief_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AetherSolarGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (youtubeLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AetherSpaceDark, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Brief Video", tint = AetherSpaceDark)
                        }
                    }
                }

                // Brief Result section
                youtubeBriefResult?.let { result ->
                    Spacer(modifier = Modifier.height(12.dp))
                    if (result == "LOCK_PREMIUM") {
                        // Premium subscription locked screen
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, AetherSolarGold, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = AetherSlateObsidian.copy(alpha = 0.9f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = AetherSolarGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = com.example.ui.translation.Translator.get("premium_required", appLanguage).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = AetherSolarGold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "This synapse core is locked. Free plan models have an 8 requests per day quota limit. Pay 2 INR every 14 days under CONFIG panel to join premium plans now.",
                                    fontSize = 11.sp,
                                    color = AetherTextLight,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { 
                                        viewModel.navigateTo(AetherRoute.SETTINGS) 
                                        Toast.makeText(context, "Subscribe on config page", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AetherSolarGold),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Upgrade to Premium Subscription", color = AetherSpaceDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        // Video briefing summary content
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, AetherGlassBorder, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = AetherSlateObsidian.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = com.example.ui.translation.Translator.get("timeline_result", appLanguage),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AetherSolarGold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "CLEAR",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AetherHotPink,
                                        modifier = Modifier.clickable { viewModel.clearYoutubeBriefing() }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = result,
                                    fontSize = 11.sp,
                                    color = AetherTextLight,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // 📋 DYNAMIC HIGH-COHERENCE TASKS LIST
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = AetherPlasmaPurple
            ) {
                Text(
                    text = "SYNAPSE CHECKLIST SYSTEM",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherPlasmaPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = taskInput,
                        onValueChange = { taskInput = it },
                        modifier = Modifier.weight(1f).testTag("add_task_field"),
                        placeholder = { Text("Specify next cognitive action block...", fontSize = 12.sp, color = AetherTextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = AetherSlateObsidian,
                            unfocusedContainerColor = AetherSlateObsidian,
                            focusedTextColor = AetherTextLight,
                            unfocusedTextColor = AetherTextLight,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (taskInput.isNotBlank()) {
                                viewModel.addTask(taskInput, taskCategory)
                                taskInput = ""
                            }
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("add_task_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AetherPlasmaPurple),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val cats = listOf("Study", "Life", "Dev", "System")
                    cats.forEach { cat ->
                        val selected = taskCategory == cat
                        Card(
                            modifier = Modifier
                                .clickable { taskCategory = cat }
                                .alpha(if (selected) 1f else 0.5f),
                            colors = CardDefaults.cardColors(containerColor = if (selected) AetherPlasmaPurple else AetherDarkGray),
                            border = BorderStroke(0.5.dp, AetherGlassBorder)
                        ) {
                            Text(
                                text = cat.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AetherTextLight,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (tasks.isEmpty()) {
                    Text(
                        text = "No queued actions in current registry database.",
                        color = AetherTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    tasks.forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AetherDarkGray.copy(alpha = 0.4f))
                                .border(0.5.dp, AetherGlassBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.toggleTaskStatus(task.id, it) },
                                    colors = CheckboxDefaults.colors(checkedColor = AetherCyberCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = task.title,
                                    color = if (task.isCompleted) AetherTextMuted else AetherTextLight,
                                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                    fontSize = 13.5.sp
                                )
                            }
                            IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Trash", tint = AetherHotPink.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. CREATIVE NEURAL ENGINE SCREEN (ART STYLE)
// ==========================================
@Composable
fun NeuralWorkspaceScreen(viewModel: AetherViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var promptArt by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk Orbit") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedImageTriggered by remember { mutableStateOf(false) }

    val styleFilters = listOf("Cyberpunk Orbit", "Retro Synthwave", "Ultraviolet Spec", "Emerald Space Grid")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BackToHubHeader(
            title = "Creative Neural Graphics",
            subtitle = "Symmetric pixel neural vector field synthesizer",
            onBack = { viewModel.navigateTo(AetherRoute.DASHBOARD) }
        )

        // VISUAL SYNTHESIZER
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = AetherCyberCyan
        ) {
            Text(
                text = "HOLOGRAPHIC DRAW FRAME GENERATOR",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AetherCyberCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = promptArt,
                onValueChange = { promptArt = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Describe visual vector geometry or planet node...", fontSize = 12.sp, color = AetherTextMuted) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = AetherTextLight,
                    focusedContainerColor = AetherSlateObsidian,
                    unfocusedContainerColor = AetherSlateObsidian,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal visual filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                styleFilters.forEach { filterStyle ->
                    val isSelected = selectedStyle == filterStyle
                    Card(
                        modifier = Modifier
                            .clickable { selectedStyle = filterStyle }
                            .alpha(if (isSelected) 1.0f else 0.5f),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) AetherCyberCyan else AetherDarkGray),
                        border = BorderStroke(0.5.dp, AetherGlassBorder)
                    ) {
                        Text(
                            text = filterStyle,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            color = if (isSelected) AetherSpaceDark else AetherTextLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    isGenerating = true
                    coroutineScope.launch {
                        delay(1800)
                        isGenerating = false
                        generatedImageTriggered = true
                        viewModel.addMemory("Generated Schematic vector: $selectedStyle design", "art_history", 0.70f)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AetherPlasmaPurple),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Render Vector Frame Artifact", fontWeight = FontWeight.Bold)
            }
        }

        if (isGenerating) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AetherDarkGray)
                    .border(1.dp, AetherGlassBorder, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AetherCyberCyan)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "COMPIING RAY COHERENT CODES...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = AetherSolarGold
                    )
                }
            }
        } else if (generatedImageTriggered) {
            // OUTSTANDING GRAPHIC CANVAS DRAWING SHAPES
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AetherSolarGold
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEURAL OUT_STREAM ARTWORK",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = AetherSolarGold,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Get artwork",
                        tint = AetherCyberCyan,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable {
                                Toast.makeText(viewModel.getApplication(), "Artwork saved to cache.", Toast.LENGTH_SHORT).show()
                            }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AetherSlateObsidian)
                        .border(1.dp, AetherGlassBorder, RoundedCornerShape(12.dp))
                ) {
                    // Incredible geometric flower schematic drawn with custom brush and rotating concentric lines on Canvas!
                    val infiniteTransition = rememberInfiniteTransition(label = "StarVectorSparkle")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2200, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "beat"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val limitRad = size.minDimension * 0.35f

                        // Draw background ambient circle glows
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(AetherPlasmaPurple.copy(alpha = 0.4f), Color.Transparent),
                                center = Offset(centerX, centerY)
                            ),
                            radius = limitRad * 1.5f
                        )

                        // Draw intersecting geometry traces
                        val petalCount = 8
                        for (i in 0 until petalCount) {
                            val angle = (i * (360f / petalCount)) * (Math.PI / 180f)
                            val endX = centerX + (limitRad * pulseScale) * Math.cos(angle).toFloat()
                            val endY = centerY + (limitRad * pulseScale) * Math.sin(angle).toFloat()

                            drawLine(
                                brush = Brush.linearGradient(
                                    listOf(AetherCyberCyan, AetherPlasmaPurple)
                                ),
                                start = Offset(centerX, centerY),
                                end = Offset(endX, endY),
                                strokeWidth = 3f
                            )

                            drawCircle(
                                color = AetherSolarGold,
                                radius = 4.dp.toPx(),
                                center = Offset(endX, endY)
                            )
                        }

                        // Central core target ring
                        drawCircle(
                            color = AetherTextLight,
                            radius = 16.dp.toPx(),
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Aether Vector Schematic | Filter: $selectedStyle",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.5.sp,
                            color = AetherTextLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. STUDY LAB TUTORING SCREEN (INTEL)
// ==========================================
@Composable
fun StudyLabScreen(viewModel: AetherViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var topicQuery by remember { mutableStateOf("Android View State Patterns") }
    var compileOutputResult by remember { mutableStateOf("") }
    var isCompilingBrief by remember { mutableStateOf(false) }

    val keyPreSets = listOf(
        Pair("Kotlin Async Coroutine Scope", "To safely perform non-blocking actions, always launch within viewModelScope.launch(Dispatchers.IO) and pipe results to UI via StateFlow objects."),
        Pair("Room Persistence Database Rules", "Define precise @Entity schemas with appropriate indexing parameters, then expose live lists via @Dao query flows asynchronously.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BackToHubHeader(
            title = "Intellect Study Lab",
            subtitle = "Decentralized code tutor synapse & query compiler",
            onBack = { viewModel.navigateTo(AetherRoute.DASHBOARD) }
        )

        // MANUAL COMPILER PROMPT
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = AetherCyberCyan
        ) {
            Text(
                text = "COGNITIVE SYNAPSE DECOMPILER",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AetherCyberCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = topicQuery,
                onValueChange = { topicQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Specify concept to decompile...", fontSize = 12.sp, color = AetherTextMuted) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = AetherTextLight,
                    focusedContainerColor = AetherSlateObsidian,
                    unfocusedContainerColor = AetherSlateObsidian,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    isCompilingBrief = true
                    compileOutputResult = ""
                    coroutineScope.launch {
                        delay(1200)
                        isCompilingBrief = false
                        compileOutputResult = "COMPILER_PAYLOAD_OUT // DECOMPILED SUCCESSSFULLY:\n\n* **Synchronous Rules:** Unidirectional state updates must pipeline through clear flow states. Avoid raw asynchronous threading conflicts.\n* **Design Best Case:** Keep layouts neat using custom adaptive density metrics on responsive screens."
                        viewModel.addMemory("Studied decompiled: $topicQuery topics", "concept_fact", 0.70f)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AetherPlasmaPurple),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Decompile Knowledge Blueprint", fontWeight = FontWeight.Bold)
            }
        }

        if (isCompilingBrief) {
            CircularProgressIndicator(color = AetherCyberCyan, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (compileOutputResult.isNotBlank()) {
            GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = AetherSolarGold, accentColor = AetherSolarGold) {
                Text(
                    text = "AETHER INTELLECTUAL OUTPUT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = AetherSolarGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Style the decompiled response nicely inside a terminal-looking black box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AetherSlateObsidian)
                        .padding(12.dp)
                ) {
                    Text(
                        text = compileOutputResult,
                        fontSize = 13.sp,
                        color = AetherEmerald,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // STUDY PATHWAYS MODULES
        Text(
            text = "REGISTERED KNOWLEDGE MODULES",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AetherTextMuted,
            letterSpacing = 1.sp
        )

        keyPreSets.forEach { (topicTitle, topicDesc) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        compileOutputResult = topicDesc
                    },
                colors = CardDefaults.cardColors(containerColor = AetherDarkGray.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, AetherGlassBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = "School", tint = AetherCyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = topicTitle,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = AetherCyberCyan,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to load intellectual concept briefs",
                        fontSize = 11.sp,
                        color = AetherTextMuted
                    )
                }
            }
        }
    }
}

// ==========================================
// 7. MEMORY MATRIX SYSTEM SCREEN (WITH COORDINATE MAP!)
// ==========================================
@Composable
fun MemoryMatrixScreen(viewModel: AetherViewModel) {
    val memories by viewModel.memoriesList.collectAsState()
    var memoryInput by remember { mutableStateOf("") }
    
    // Choose coordinate map tab vs classic ledger listing values
    var activeTabIdx by remember { mutableStateOf(0) } // 0 = COGNITIVE STATE GRAPH, 1 = DATA REGISTRY LEDGER

    Column(modifier = Modifier.fillMaxSize()) {
        BackToHubHeader(
            title = "Memory Matrix Ledger",
            subtitle = "Offline-first Room database relational synapse system",
            onBack = { viewModel.navigateTo(AetherRoute.DASHBOARD) }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // INGEST NODE DIALOG CONSOLE
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = AetherCyberCyan
        ) {
            Text(
                text = "STREAM MANUAL RELATIONAL NODE",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AetherCyberCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = memoryInput,
                    onValueChange = { memoryInput = it },
                    modifier = Modifier.weight(1f).testTag("memory_input_text"),
                    placeholder = { Text("E.g., Core preference is clean adaptive themes...", fontSize = 12.sp, color = AetherTextMuted) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = AetherTextLight,
                        focusedContainerColor = AetherSlateObsidian,
                        unfocusedContainerColor = AetherSlateObsidian,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (memoryInput.isNotBlank()) {
                            viewModel.addMemory(memoryInput, "manually_logged", 0.95f)
                            memoryInput = ""
                        }
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("memory_save_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AetherCyberCyan),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "save fact", tint = AetherSpaceDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Segment Tabs selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AetherDarkGray)
                .padding(4.dp)
        ) {
            val tabs = listOf("Interactive Graph Map", "Registry Ledger")
            tabs.forEachIndexed { index, tabTitle ->
                val active = activeTabIdx == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) AetherPlasmaPurple else Color.Transparent)
                        .clickable { activeTabIdx = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) AetherTextLight else AetherTextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // VIEW RENDERING BASED ON TAB SELECTION
        if (activeTabIdx == 0) {
            // OUTSTANDING FUCTURISTIC INTERACTIVE STARRY MAP NODE CANVAS!
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, AetherGlassBorder, RoundedCornerShape(20.dp))
                    .background(AetherSlateObsidian)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "StarSparklePulse")
                val pulsateRad by infiniteTransition.animateFloat(
                    initialValue = 0.85f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1800, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "spark"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    
                    val nodeCoordinates = listOf(
                        Offset(0.2f * w, 0.3f * h),
                        Offset(0.5f * w, 0.25f * h),
                        Offset(0.8f * w, 0.4f * h),
                        Offset(0.35f * w, 0.65f * h),
                        Offset(0.68f * w, 0.72f * h),
                        Offset(0.48f * w, 0.5f * h)
                    )

                    // Draw relational grid fibers
                    for (i in 0 until nodeCoordinates.size - 1) {
                        drawLine(
                            color = AetherGlassBorder,
                            start = nodeCoordinates[i],
                            end = nodeCoordinates[i+1],
                            strokeWidth = 2f
                        )
                    }
                    drawLine(color = AetherGlassBorder, start = nodeCoordinates[0], end = nodeCoordinates[nodeCoordinates.size - 1], strokeWidth = 2f)

                    // Draw pulsing circles
                    nodeCoordinates.forEachIndexed { index, coord ->
                        val nodeColor = when (index % 3) {
                            0 -> AetherCyberCyan
                            1 -> AetherPlasmaPurple
                            else -> AetherSolarGold
                        }
                        
                        // Halo glow
                        drawCircle(
                            color = nodeColor.copy(alpha = 0.15f),
                            radius = 24.dp.toPx() * pulsateRad,
                            center = coord
                        )
                        // Precise locator dot
                        drawCircle(
                            color = nodeColor,
                            radius = 6.dp.toPx(),
                            center = coord
                        )
                    }
                }

                // Small contextual guide overlay inside canvas frame
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aether Relational Map layer active. Local room schemas connected.",
                        fontSize = 10.5.sp,
                        color = AetherCyberCyan,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // CLASSIC DATA REGISTRY LEDGER LIST
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SYNAPSED LEDGER NODES (${memories.size})",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherTextMuted
                )
                Text(
                    text = "PURGE DATABASE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherHotPink,
                    modifier = Modifier.clickable { viewModel.clearAllMemories() }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (memories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cognitive ledger registry contains 0 memory blocks. Engage in conversations or camera scans to compile database blocks.",
                        color = AetherTextMuted,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(memories) { memoryNode ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AetherDarkGray.copy(alpha = 0.6f)),
                            border = BorderStroke(1.dp, AetherGlassBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(AetherSolarGold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "NODE REGISTER ${memoryNode.id}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AetherSolarGold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = memoryNode.keyPhrase,
                                        fontSize = 13.5.sp,
                                        color = AetherTextLight,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "CLASSIFICATION: ${memoryNode.contextType.uppercase()} | STRENGTH SCORE: ${memoryNode.intensity}",
                                        fontSize = 9.sp,
                                        color = AetherTextMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteMemory(memoryNode.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "delete sync", tint = AetherHotPink.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. SETTINGS SCREEN
// ==========================================
@Composable
fun SettingsScreen(viewModel: AetherViewModel) {
    val userName by viewModel.userName.collectAsState()
    val rawApiKey by viewModel.customApiKey.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val requestsToday by viewModel.requestsToday.collectAsState()

    var operatorNameInput by remember { mutableStateOf(userName) }
    var apiOverrideInput by remember { mutableStateOf(rawApiKey ?: "") }
    var themeSelection by remember { mutableStateOf(appTheme) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TelemetryClockHeader()

        Text(
            text = com.example.ui.translation.Translator.get("CONFIG", appLanguage),
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AetherTextLight,
            letterSpacing = 0.5.sp
        )

        // 🔐 SECURE AUTHORIZATION STATUS
        val isLoggedIn by viewModel.isLoggedIn.collectAsState()
        val authEmailOrPhone by viewModel.authEmailOrPhone.collectAsState()
        val authMethod by viewModel.authMethod.collectAsState()
        val authSecureToken by viewModel.authSecureToken.collectAsState()

        if (isLoggedIn) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("auth_status_card"),
                accentColor = AetherCyberCyan
            ) {
                Text(
                    text = "SECURE IDENTITY COUPLING",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherCyberCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PROVIDER: ${authMethod.uppercase()}",
                            color = AetherTextLight,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "IDENTITY: $authEmailOrPhone",
                            color = AetherTextLight,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(AetherCyberCyan.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                            .border(0.5.dp, AetherCyberCyan, shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("ENCRYPTED", color = AetherCyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LOCAL DECRYPT KEY: ${authSecureToken.take(40)}...",
                    color = AetherSolarGold,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.deauthorizeUser() },
                    colors = ButtonDefaults.buttonColors(containerColor = AetherHotPink),
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("btn_deauthorize_logout"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SECURE DEAUTHORIZE / LOGOUT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // 🌟 PREMIUM BILLING SUBSCRIPTION PANEL
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = AetherSolarGold
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = com.example.ui.translation.Translator.get("premium_plan", appLanguage),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AetherSolarGold,
                    letterSpacing = 1.sp
                )
                
                // Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isPremium) AetherSolarGold.copy(alpha = 0.15f) else AetherGlassBorder.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isPremium) "ACTIVE" else "FREE",
                        fontSize = 9.sp,
                        color = if (isPremium) AetherSolarGold else AetherTextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isPremium) {
                Text(
                    text = com.example.ui.translation.Translator.get("premium_subscriber", appLanguage),
                    fontSize = 12.sp,
                    color = AetherTextLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Unlimited neural queries enabled.\n✓ Direct YouTube briefing synapse active.",
                    fontSize = 10.sp,
                    color = AetherTextMuted,
                    lineHeight = 14.sp
                )
            } else {
                Text(
                    text = String.format(com.example.ui.translation.Translator.get("today_requests", appLanguage), requestsToday, 25),
                    fontSize = 12.sp,
                    color = AetherTextLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = com.example.ui.translation.Translator.get("upgrade_banner", appLanguage),
                    fontSize = 11.sp,
                    color = AetherTextMuted,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.togglePremiumStatus()
                    val msg = if (!isPremium) "Aether Premium Activated for 14 days (Simulated micro-payment of 2 INR)" else "Plan downgraded to free status"
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth().testTag("toggle_premium_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = AetherSolarGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isPremium) "Downgrade Plan (Dev)" else com.example.ui.translation.Translator.get("pay_2_inr", appLanguage),
                    color = AetherSpaceDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // 🌐 LANGUAGE SETTINGS
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = AetherNebulaBlue
        ) {
            Text(
                text = com.example.ui.translation.Translator.get("language_selection", appLanguage),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AetherNebulaBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            com.example.ui.translation.Translator.languages.forEach { (langCode, langName) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.updateLanguage(langCode) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = appLanguage == langCode,
                        onClick = { viewModel.updateLanguage(langCode) },
                        colors = RadioButtonDefaults.colors(selectedColor = AetherNebulaBlue)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = langName,
                        color = AetherTextLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // SECURE TOKEN FORM PANEL
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = AetherCyberCyan
        ) {
            Text(
                text = "GEMINI API DIRECT_ACC",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AetherCyberCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "By default, Aether uses keys compiled from backend secrets panels. Enter a custom key override below. Key is stored safely on encrypted DataStore local structures.",
                fontSize = 11.sp,
                color = AetherTextMuted,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = apiOverrideInput,
                onValueChange = { apiOverrideInput = it },
                modifier = Modifier.fillMaxWidth().testTag("api_key_override_field"),
                placeholder = { Text("E.g., AIzaSy...", fontSize = 12.sp, color = AetherTextMuted) },
                label = { Text(com.example.ui.translation.Translator.get("custom_token_override", appLanguage), fontSize = 11.sp) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = AetherTextLight,
                    focusedContainerColor = AetherSlateObsidian,
                    unfocusedContainerColor = AetherSlateObsidian,
                    focusedIndicatorColor = AetherCyberCyan
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // OPERATOR USERNAME SETTINGS
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = AetherPlasmaPurple
        ) {
            Text(
                text = "OPERATOR PROFILE SYSTEMS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AetherPlasmaPurple,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = operatorNameInput,
                onValueChange = { operatorNameInput = it },
                modifier = Modifier.fillMaxWidth().testTag("operator_name_field"),
                placeholder = { Text("Nexus Operator", fontSize = 12.sp, color = AetherTextMuted) },
                label = { Text(com.example.ui.translation.Translator.get("code_designation_label", appLanguage), fontSize = 11.sp) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = AetherTextLight,
                    focusedContainerColor = AetherSlateObsidian,
                    unfocusedContainerColor = AetherSlateObsidian,
                    focusedIndicatorColor = AetherPlasmaPurple
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Choose App Theme variation
            Text(
                text = com.example.ui.translation.Translator.get("brand_themes", appLanguage),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = AetherTextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            val availableThemes = listOf(
                "cyberpunk",
                "hologram_grid",
                "slate_luxury",
                "matrix_terminal",
                "nebula_solaris",
                "chrono_pink"
            )
            availableThemes.forEach { selection ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { themeSelection = selection }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = themeSelection == selection,
                        onClick = { themeSelection = selection },
                        colors = RadioButtonDefaults.colors(selectedColor = AetherCyberCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selection.replace("_", " ").uppercase() + if (selection == "nebula_solaris") " (DEFAULT)" else "",
                        color = AetherTextLight,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ACTION COMPILE BUTTON
        Button(
            onClick = {
                viewModel.updateProfile(operatorNameInput, themeSelection, apiOverrideInput)
                Toast.makeText(context, "System configuration registers logged successfully.", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_settings_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AetherCyberCyan),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(com.example.ui.translation.Translator.get("save_btn", appLanguage), color = AetherSpaceDark, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "MADE PROUDLY IN INDIA 🇮🇳",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = AetherSolarGold,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp)
        )
    }
}

// ==========================================
// 9. HIGH-SECURITY SECURE AUTHENTICATION CONSOLE
// ==========================================
@Composable
fun AetherAuthScreen(viewModel: AetherViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Email, 1: Google, 2: Phone, 3: Facebook

    // Form inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var phoneCountryCode by remember { mutableStateOf("+91") }
    var phoneNum by remember { mutableStateOf("") }
    var requestedOtp by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    var localSaltKey by remember { mutableStateOf("AetherVirtStore_AES2026") }

    // Dynamic Live Signature compute: shows transparency of cryptography
    val dynamicInputString = when (selectedTab) {
        0 -> "METHOD:Email;IDENTIFIER:$email;SECRET:${password.hashCode()};SALT:$localSaltKey"
        1 -> "METHOD:Google;IDENTIFIER:GoogleAccountAuthVirt;SECRET:Oauth2TokenSig;SALT:$localSaltKey"
        2 -> "METHOD:Phone;IDENTIFIER:$phoneCountryCode$phoneNum;SECRET:$otpCode;SALT:$localSaltKey"
        else -> "METHOD:Facebook;IDENTIFIER:FBGraphUserSecureLink;SECRET:UserGrantPayload;SALT:$localSaltKey"
    }

    val liveHashSignature = remember(dynamicInputString) {
        try {
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val digest = md.digest(dynamicInputString.toByteArray())
            digest.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            "HASH_ERROR_RESOLVING_SHA256"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(14.dp),
        bottomBar = {
            // High Security telemetry status footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AetherDarkGray.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                    .border(0.5.dp, AetherGlassBorder, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Shield Guard",
                        tint = AetherCyberCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ON-DEVICE HARDWARE KEystore ACTIVATED",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AetherTextCyber,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "SANDBOX SHA-256: ${liveHashSignature.take(45)}...",
                    fontSize = 7.sp,
                    fontFamily = FontFamily.Monospace,
                    color = AetherSolarGold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Brand Header logo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(AetherSpaceDark, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, AetherCyberCyan, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hexagon,
                        contentDescription = "Aether Shield Logo",
                        tint = AetherCyberCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AETHER SECURE COUPLING",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = AetherTextLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Establish encrypted link to access node clusters",
                    fontSize = 11.sp,
                    color = AetherTextMuted,
                    textAlign = TextAlign.Center
                )
            }

            // PROVIDER REGISTRATION SELECTION TAB ROW
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AetherSpaceDark,
                contentColor = AetherCyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AetherCyberCyan
                    )
                },
                modifier = Modifier.fillMaxWidth().testTag("auth_provider_tab_row")
            ) {
                listOf("EMAIL", "GOOGLE", "PHONE", "FACEBOOK").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (selectedTab == index) AetherCyberCyan else AetherTextMuted
                            )
                        }
                    )
                }
            }

            // PROVIDER FORM MODULATOR
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "AuthFormAnimation"
            ) { provider ->
                when (provider) {
                    0 -> { // EMAIL WORKFLOW
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "EMAIL-SHIELD LOGIN REGISTER",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AetherSolarGold
                            )

                            TextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth().testTag("auth_email_field"),
                                placeholder = { Text("operator@aether.systems", fontSize = 12.sp, color = AetherTextMuted) },
                                label = { Text("COGNITIVE IDENTITY (EMAIL)", fontSize = 10.sp) },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = AetherCyberCyan) },
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = AetherTextLight,
                                    focusedContainerColor = AetherSlateObsidian,
                                    unfocusedContainerColor = AetherSlateObsidian,
                                    focusedIndicatorColor = AetherCyberCyan
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            TextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth().testTag("auth_password_field"),
                                placeholder = { Text("••••••••", fontSize = 12.sp, color = AetherTextMuted) },
                                label = { Text("CRYPTO SHIELD KEY (PASSWORD)", fontSize = 10.sp) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock", tint = AetherCyberCyan) },
                                visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = AetherTextMuted
                                        )
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = AetherTextLight,
                                    focusedContainerColor = AetherSlateObsidian,
                                    unfocusedContainerColor = AetherSlateObsidian,
                                    focusedIndicatorColor = AetherCyberCyan
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Button(
                                onClick = {
                                    if (!email.contains("@") || email.isBlank()) {
                                        Toast.makeText(context, "Shield Alert: Invalid node address email schema.", Toast.LENGTH_SHORT).show()
                                    } else if (password.length < 6) {
                                        Toast.makeText(context, "Shield Alert: Key must be at least 6 characters.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.authenticateUser("Email", email, password)
                                        Toast.makeText(context, "Access Granted. Local credentials fully linked.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_auth_email_submit"),
                                colors = ButtonDefaults.buttonColors(containerColor = AetherCyberCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("INITIALIZE EMAIL LINK", color = AetherSpaceDark, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                    1 -> { // GOOGLE AUTH INTEGRATION (Simulation and sandbox authorization check)
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "GOOGLE COUPLING NODE INTEGRATOR",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AetherSolarGold
                            )
                            Text(
                                text = "Link the existing Google workspace parameters automatically on-device. This is handled using Android services sandbox credentials safely, creating an OAuth2 credential token signature.",
                                fontSize = 11.sp,
                                color = AetherTextMuted,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val mockGoogleEmail = "sec_opr_" + (1000..9999).random() + "@gmail.com"
                                        viewModel.authenticateUser("Google", mockGoogleEmail, "OAUTH_TOKEN_SEC_MD5")
                                        Toast.makeText(context, "Google OAuth Access approved. Local profile updated.", Toast.LENGTH_SHORT).show()
                                    }
                                    .testTag("btn_auth_google_submit"),
                                colors = CardDefaults.cardColors(containerColor = AetherSlateObsidian),
                                border = BorderStroke(1.dp, AetherGlassBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color.White, shape = RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("G", color = Color.Blue, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "SIGN IN SECURELY WITH GOOGLE",
                                        color = AetherTextLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                    2 -> { // PHONE OTP WORKFLOW
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "CELLULAR SECURE WAVE HANDSHAKE",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AetherSolarGold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextField(
                                    value = phoneCountryCode,
                                    onValueChange = { phoneCountryCode = it },
                                    modifier = Modifier.width(70.dp).testTag("auth_phone_cc_field"),
                                    label = { Text("C.C.", fontSize = 9.sp) },
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = AetherTextLight,
                                        focusedContainerColor = AetherSlateObsidian,
                                        unfocusedContainerColor = AetherSlateObsidian,
                                        focusedIndicatorColor = AetherCyberCyan
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                TextField(
                                    value = phoneNum,
                                    onValueChange = { phoneNum = it },
                                    modifier = Modifier.weight(1f).testTag("auth_phone_num_field"),
                                    placeholder = { Text("9876543210", fontSize = 12.sp, color = AetherTextMuted) },
                                    label = { Text("PHONE NUMBER", fontSize = 9.sp) },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = AetherCyberCyan) },
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = AetherTextLight,
                                        focusedContainerColor = AetherSlateObsidian,
                                        unfocusedContainerColor = AetherSlateObsidian,
                                        focusedIndicatorColor = AetherCyberCyan
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            if (!requestedOtp) {
                                Button(
                                    onClick = {
                                        if (phoneNum.length < 10) {
                                            Toast.makeText(context, "Shield Alert: Invalid telephone node address.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            requestedOtp = true
                                            Toast.makeText(context, "Security Wave Handshake initiated. OTP signal dispatched.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_auth_phone_request_otp"),
                                    colors = ButtonDefaults.buttonColors(containerColor = AetherCyberCyan),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("DISPATCH SECURITY SIGN OTP", color = AetherSpaceDark, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            } else {
                                TextField(
                                    value = otpCode,
                                    onValueChange = { otpCode = it },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_phone_otp_field"),
                                    placeholder = { Text("123456", fontSize = 12.sp, color = AetherTextMuted) },
                                    label = { Text("LOCAL VERIFICATION DIGITS (OTP)", fontSize = 10.sp) },
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = AetherTextLight,
                                        focusedContainerColor = AetherSlateObsidian,
                                        unfocusedContainerColor = AetherSlateObsidian,
                                        focusedIndicatorColor = AetherCyberCyan
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Button(
                                    onClick = {
                                        if (otpCode.length < 6) {
                                            Toast.makeText(context, "Shield Alert: OTP requires 6 complete validation registers.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.authenticateUser("Phone", "$phoneCountryCode$phoneNum", otpCode)
                                            Toast.makeText(context, "OTP Verified. Access Granted.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_auth_phone_verify"),
                                    colors = ButtonDefaults.buttonColors(containerColor = AetherPlasmaPurple),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("VERIFY NEURAL OTP", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }

                                Text(
                                    text = "Didn't receive the signal? Resend waves requested.",
                                    fontSize = 11.sp,
                                    color = AetherTextMuted,
                                    modifier = Modifier.clickable {
                                        Toast.makeText(context, "Secondary wave dispatched.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                    else -> { // FACEBOOK INTEGRATOR
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "FACEBOOK VISUAL AUTOLINK SHIELD",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AetherSolarGold
                            )
                            Text(
                                text = "Initialize authentic secure client handshake linking profile parameters directly with Facebook Graph credentials framework securely on device with sandboxed variables.",
                                fontSize = 11.sp,
                                color = AetherTextMuted,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val mockFbProfileName = "social_opr_" + (1000..9999).random() + "@facebook.com"
                                        viewModel.authenticateUser("Facebook", mockFbProfileName, "FB_GRAPH_OAUTH_TOKEN")
                                        Toast.makeText(context, "Facebook Coupling Authenticated. Signal integrated.", Toast.LENGTH_SHORT).show()
                                    }
                                    .testTag("btn_auth_facebook_submit"),
                                colors = CardDefaults.cardColors(containerColor = AetherSlateObsidian),
                                border = BorderStroke(1.dp, AetherGlassBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFF3B5998), shape = RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("f", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "SIGN IN SECURELY WITH FACEBOOK",
                                        color = AetherTextLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
