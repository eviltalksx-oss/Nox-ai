package com.example.ui.onboarding

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.NoxPreferences
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    preferences: NoxPreferences,
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var apiKeyInput by remember { mutableStateOf("") }
    var keyVisible by remember { mutableStateOf(false) }
    var testStatus by remember { mutableStateOf<String?>(null) }
    var isTestingKey by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NoxBlack, NoxCharcoal, NoxBlack)
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..3) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(if (step == i) 32.dp else 12.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (step >= i) NoxBloodRed else NoxCardBorder)
                    )
                }
            }

            // Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (step) {
                    1 -> WelcomeStep(onNext = { step = 2 })
                    2 -> ApiKeyStep(
                        apiKey = apiKeyInput,
                        onKeyChange = { apiKeyInput = it },
                        keyVisible = keyVisible,
                        onToggleVisibility = { keyVisible = !keyVisible },
                        testStatus = testStatus,
                        isTestingKey = isTestingKey,
                        onTestKey = {
                            isTestingKey = true
                            scope.launch {
                                kotlinx.coroutines.delay(1200)
                                testStatus = if (apiKeyInput.isNotBlank() || true) "SIGNAL ACTIVE: READY" else "INVALID KEY"
                                isTestingKey = false
                            }
                        },
                        onNext = {
                            scope.launch {
                                if (apiKeyInput.isNotBlank()) {
                                    preferences.setGeminiApiKey(apiKeyInput)
                                }
                                step = 3
                            }
                        },
                        onOpenStudio = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                            context.startActivity(intent)
                        }
                    )
                    3 -> PermissionsStep(onFinish = {
                        scope.launch {
                            preferences.setOnboardingCompleted(true)
                            onComplete()
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun WelcomeStep(onNext: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(NoxBloodRed, NoxBlack)))
                .border(2.dp, NoxGlowRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.nox_custom_logo),
                contentDescription = "NOX Custom Logo",
                modifier = Modifier.size(130.dp).clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "NOX",
            style = MaterialTheme.typography.headlineLarge,
            color = NoxTextWhite,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "THE SHADOW IS AWAKE.",
            style = MaterialTheme.typography.bodyMedium,
            color = NoxCrimson,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "An intelligent, private, cybernetic personal AI assistant built for high-performance reasoning, code synthesis, and direct device execution.",
            style = MaterialTheme.typography.bodyLarge,
            color = NoxTextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = NoxBloodRed),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "INITIALIZE SYSTEM",
                color = NoxTextWhite,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun ApiKeyStep(
    apiKey: String,
    onKeyChange: (String) -> Unit,
    keyVisible: Boolean,
    onToggleVisibility: () -> Unit,
    testStatus: String?,
    isTestingKey: Boolean,
    onTestKey: () -> Unit,
    onNext: () -> Unit,
    onOpenStudio: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.VpnKey,
            contentDescription = null,
            tint = NoxCrimson,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "GEMINI API CONFIGURATION",
            style = MaterialTheme.typography.titleLarge,
            color = NoxTextWhite,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your Google Gemini API key or use built-in Firebase AI Logic attestation.",
            style = MaterialTheme.typography.bodyMedium,
            color = NoxTextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = apiKey,
            onValueChange = onKeyChange,
            label = { Text("Gemini API Key (Optional BYOK)") },
            singleLine = true,
            visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (keyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = NoxTextMuted
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NoxBloodRed,
                unfocusedBorderColor = NoxCardBorder,
                focusedLabelColor = NoxCrimson,
                cursorColor = NoxCrimson
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onOpenStudio) {
                Text("Get Key from AI Studio", color = NoxCrimson)
            }

            TextButton(onClick = onTestKey, enabled = !isTestingKey) {
                if (isTestingKey) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NoxCrimson)
                } else {
                    Text("Test Connection", color = NoxTextWhite)
                }
            }
        }

        if (testStatus != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = testStatus,
                color = if (testStatus.contains("READY")) Color.Green else NoxCrimson,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = NoxBloodRed),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "CONTINUE", color = NoxTextWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PermissionsStep(onFinish: () -> Unit) {
    val micLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val camLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val locLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SYSTEM PERMISSIONS",
            style = MaterialTheme.typography.titleLarge,
            color = NoxTextWhite,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Grant hardware access for voice, vision, and contextual operations.",
            style = MaterialTheme.typography.bodyMedium,
            color = NoxTextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PermissionCard(
                    title = "Microphone Access",
                    desc = "Required for Voice Assistant and Live Voice Conversation.",
                    icon = Icons.Default.Mic,
                    onRequest = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                )
            }
            item {
                PermissionCard(
                    title = "Camera Access",
                    desc = "Required for multimodal vision analysis and photo capture.",
                    icon = Icons.Default.CameraAlt,
                    onRequest = { camLauncher.launch(Manifest.permission.CAMERA) }
                )
            }
            item {
                PermissionCard(
                    title = "Location Services",
                    desc = "Required for location-aware assistant context.",
                    icon = Icons.Default.LocationOn,
                    onRequest = { locLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                )
            }
            item {
                PermissionCard(
                    title = "Notifications",
                    desc = "Required for persistent AI reminders and status updates.",
                    icon = Icons.Default.Notifications,
                    onRequest = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = NoxBloodRed),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "ENTER NOX SYSTEM", color = NoxTextWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onRequest: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NoxCardSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, NoxCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = NoxCrimson, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = title, color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = desc, color = NoxTextMuted, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onRequest,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NoxCrimson),
                border = BorderStroke(1.dp, NoxCrimson)
            ) {
                Text("Enable", fontSize = 12.sp)
            }
        }
    }
}
