package com.example.ui.voice

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceAssistantScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var voiceMode by remember { mutableStateOf("Live Voice Conversation") }
    var transcript by remember { mutableStateOf("NOX voice core active. Speak or tap orb to start listening.") }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "voice")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voice_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NOX VOICE ASSISTANT", color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NoxTextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NoxCharcoal)
            )
        },
        containerColor = NoxBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mode selector
            Row(
                modifier = Modifier
                    .background(NoxCardSurface, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Live Voice Conversation", "Standard TTS").forEach { mode ->
                    val selected = voiceMode == mode
                    Surface(
                        onClick = { voiceMode = mode },
                        color = if (selected) NoxBloodRed else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = mode,
                            color = if (selected) NoxTextWhite else NoxTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Central Listening / Speaking Orb
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(if (isListening || isSpeaking) scale else 1f)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(NoxBloodRed, NoxDarkRed, NoxBlack)))
                        .border(3.dp, NoxGlowRed, CircleShape)
                        .clickable {
                            isListening = !isListening
                            if (isListening) {
                                transcript = "Listening for audio signal..."
                            } else {
                                transcript = "NOX: Circuit online. Standing by for instructions."
                                tts?.speak(transcript, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                        contentDescription = null,
                        tint = NoxTextWhite,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = if (isListening) "LISTENING..." else if (isSpeaking) "SPEAKING..." else "TAP TO SPEAK",
                    color = NoxCrimson,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = NoxCardSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, NoxCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = transcript,
                        color = NoxTextWhite,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        tts?.stop()
                        isSpeaking = false
                        isListening = false
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(NoxCardSurface, CircleShape)
                        .border(1.dp, NoxCardBorder, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop", tint = NoxCrimson)
                }

                Button(
                    onClick = {
                        isSpeaking = true
                        val text = "-- made by NOX --. The shadow is awake. Voice parameters locked to deep mature masculine baritone."
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NoxBloodRed),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(52.dp).width(180.dp)
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = NoxTextWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SPEAK NOX", color = NoxTextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
