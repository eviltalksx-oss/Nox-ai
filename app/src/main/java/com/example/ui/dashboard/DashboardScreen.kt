package com.example.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    displayName: String,
    onNavigateChat: () -> Unit,
    onNavigateVoice: () -> Unit,
    onNavigateCode: () -> Unit,
    onNavigateFiles: () -> Unit,
    onNavigateDevice: () -> Unit,
    onNavigateMemory: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    var promptInput by remember { mutableStateOf("") }

    // Pulsing animation for the AI orb
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NoxBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NoxBloodRed)
                            .border(1.dp, NoxGlowRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nox_custom_logo),
                            contentDescription = "NOX Logo",
                            modifier = Modifier.size(38.dp).clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "NOX",
                            color = NoxTextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ONLINE • SHADOW INTELLIGENCE",
                                color = NoxTextMuted,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onNavigateMemory,
                        modifier = Modifier
                            .size(40.dp)
                            .background(NoxCardSurface, CircleShape)
                            .border(1.dp, NoxCardBorder, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Memory, contentDescription = "Memory", tint = NoxCrimson, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = onNavigateSettings,
                        modifier = Modifier
                            .size(40.dp)
                            .background(NoxCardSurface, CircleShape)
                            .border(1.dp, NoxCardBorder, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = NoxTextWhite, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Central Area with Pulsing Orb & Greeting
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Pulsing Crimson AI Orb with Custom User Logo
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(NoxBloodRed, NoxDarkRed, NoxBlack)))
                        .border(2.dp, NoxGlowRed, CircleShape)
                        .clickable { onNavigateChat() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nox_custom_logo),
                        contentDescription = "NOX Cybernetic Eye",
                        modifier = Modifier.size(160.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Welcome back, $displayName.",
                    color = NoxTextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Neural circuits locked. What are we breaking or building today?",
                    color = NoxTextMuted,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Action Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        listOf(
                            Triple("New Chat", Icons.Default.Chat, onNavigateChat),
                            Triple("Voice", Icons.Default.Mic, onNavigateVoice),
                            Triple("Code", Icons.Default.Code, onNavigateCode),
                            Triple("Files", Icons.Default.AttachFile, onNavigateFiles),
                            Triple("Device", Icons.Default.PhoneAndroid, onNavigateDevice)
                        )
                    ) { (title, icon, action) ->
                        ActionChip(title = title, icon = icon, onClick = action)
                    }
                }
            }

            // Bottom Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NoxCardSurface, RoundedCornerShape(28.dp))
                    .border(1.dp, NoxCardBorder, RoundedCornerShape(28.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateFiles) {
                    Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach", tint = NoxTextMuted)
                }

                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text("Ask NOX anything...", color = NoxTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedTextColor = NoxTextWhite,
                        unfocusedTextColor = NoxTextWhite
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                )

                IconButton(onClick = onNavigateVoice) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice", tint = NoxCrimson)
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            onNavigateChat()
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(NoxBloodRed, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = NoxTextWhite, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ActionChip(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = NoxCardSurface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, NoxCardBorder),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NoxCrimson, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, color = NoxTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
