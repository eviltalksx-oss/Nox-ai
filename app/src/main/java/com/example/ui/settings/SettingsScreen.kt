package com.example.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DEFAULT_NOX_PROMPT
import com.example.data.NoxPreferences
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: NoxPreferences,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val apiKey by preferences.geminiApiKeyFlow.collectAsState(initial = "")
    val displayName by preferences.displayNameFlow.collectAsState(initial = "AIwolfie")
    val customPrompt by preferences.customPromptFlow.collectAsState(initial = DEFAULT_NOX_PROMPT)
    val memoryEnabled by preferences.memoryEnabledFlow.collectAsState(initial = true)

    var keyInput by remember(apiKey) { mutableStateOf(apiKey) }
    var nameInput by remember(displayName) { mutableStateOf(displayName) }
    var promptInput by remember(customPrompt) { mutableStateOf(customPrompt) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NOX SETTINGS", color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NoxTextWhite)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        scope.launch {
                            preferences.setGeminiApiKey(keyInput)
                            preferences.setDisplayName(nameInput)
                            preferences.setCustomPrompt(promptInput)
                            preferences.setMemoryEnabled(memoryEnabled)
                        }
                    }) {
                        Text("Save", color = NoxCrimson, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NoxCharcoal)
            )
        },
        containerColor = NoxBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSectionTitle("PROFILE & IDENTITY")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Operator Display Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NoxBloodRed,
                        unfocusedBorderColor = NoxCardBorder,
                        focusedTextColor = NoxTextWhite,
                        unfocusedTextColor = NoxTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                SettingsSectionTitle("GEMINI API CONFIGURATION")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text("Gemini API Key") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NoxBloodRed,
                        unfocusedBorderColor = NoxCardBorder,
                        focusedTextColor = NoxTextWhite,
                        unfocusedTextColor = NoxTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                SettingsSectionTitle("CUSTOM SYSTEM PROMPT (OWNER CONFIG)")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    label = { Text("NOX Core Persona Prompt") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NoxBloodRed,
                        unfocusedBorderColor = NoxCardBorder,
                        focusedTextColor = NoxTextWhite,
                        unfocusedTextColor = NoxTextWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            item {
                SettingsSectionTitle("ABOUT NOX")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoxCardSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NoxCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("NOX • PERSONAL AI ASSISTANT", color = NoxTextWhite, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Identity: PERSONAL AI • SHADOW INTELLIGENCE", color = NoxCrimson, fontSize = 12.sp)
                        Text("Tagline: THE SHADOW IS AWAKE.", color = NoxTextMuted, fontSize = 12.sp)
                        Text("Admin Developer: t.me/EVILTALKS", color = NoxTextMuted, fontSize = 12.sp)
                        Text("Version: 1.0.0 Stable", color = NoxTextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = NoxCrimson,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
}
