package com.example.ui.chat

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeminiAIClient
import com.example.data.NoxPreferences
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "nox"
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    preferences: NoxPreferences,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val geminiClient = remember { GeminiAIClient() }

    val customPrompt by preferences.customPromptFlow.collectAsState(initial = "")
    var inputMessage by remember { mutableStateOf("") }
    var messages = remember {
        mutableStateListOf(
            ChatMessage(sender = "nox", text = "-- made by NOX --\nNOX online. The shadow is awake. What are we running today?")
        )
    }
    var isGenerating by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    fun sendMessage() {
        val text = inputMessage.trim()
        if (text.isBlank() || isGenerating) return

        messages.add(ChatMessage(sender = "user", text = text))
        inputMessage = ""
        isGenerating = true

        scope.launch {
            val responsePlaceholder = ChatMessage(sender = "nox", text = "-- made by NOX --\n...")
            messages.add(responsePlaceholder)
            val index = messages.lastIndex

            try {
                val fullBuilder = StringBuilder()
                geminiClient.generateStreamResponse(text, customPrompt).collect { chunk ->
                    fullBuilder.append(chunk)
                    messages[index] = responsePlaceholder.copy(text = fullBuilder.toString())
                }
            } catch (e: Exception) {
                messages[index] = responsePlaceholder.copy(text = "-- made by NOX --\nError communicating with core.")
            } finally {
                isGenerating = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(NoxBloodRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NoxTextWhite))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("NOX CHAT", color = NoxTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Gemini 3.8 Flash • Neural Core", color = NoxTextMuted, fontSize = 10.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NoxTextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { messages.clear(); messages.add(ChatMessage(sender = "nox", text = "-- made by NOX --\nMemory purged. Ready.")) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = NoxCrimson)
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
        ) {
            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg, onCopy = { clipboardManager.setText(AnnotatedString(msg.text)) })
                }
            }

            // Input Bar
            Surface(
                color = NoxCharcoal,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("Message NOX...", color = NoxTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NoxBloodRed,
                            unfocusedBorderColor = NoxCardBorder,
                            focusedTextColor = NoxTextWhite,
                            unfocusedTextColor = NoxTextWhite
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { sendMessage() },
                        enabled = inputMessage.isNotBlank() && !isGenerating,
                        modifier = Modifier
                            .size(48.dp)
                            .background(if (inputMessage.isNotBlank()) NoxBloodRed else NoxCardSurface, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = NoxTextWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, onCopy: () -> Unit) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) NoxBloodRed else NoxCardSurface,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = if (!isUser) BorderStroke(1.dp, NoxCardBorder) else null,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    color = NoxTextWhite,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = NoxTextMuted, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}
