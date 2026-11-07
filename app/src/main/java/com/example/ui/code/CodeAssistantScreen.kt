package com.example.ui.code

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeminiAIClient
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeAssistantScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val geminiClient = remember { GeminiAIClient() }

    var codeInput by remember { mutableStateOf("") }
    var taskType by remember { mutableStateOf("Fix Bugs & Refactor") }
    var codeOutput by remember { mutableStateOf("-- made by NOX --\nWaiting for code payload...") }
    var isProcessing by remember { mutableStateOf(false) }

    fun processCode() {
        if (codeInput.isBlank() || isProcessing) return
        isProcessing = true
        scope.launch {
            val prompt = "Task: $taskType\n\nCode:\n$codeInput\n\nAnalyze, fix, and provide clean production code with explanation."
            val result = geminiClient.generateResponse(prompt, "You are NOX Code Assistant. Return production-grade code with markdown syntax formatting.")
            codeOutput = result
            isProcessing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NOX CODE ASSISTANT", color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Type selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Fix Bugs", "Explain", "Optimize").forEach { task ->
                    val selected = taskType == task
                    FilterChip(
                        selected = selected,
                        onClick = { taskType = task },
                        label = { Text(task, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NoxBloodRed,
                            selectedLabelColor = NoxTextWhite,
                            containerColor = NoxCardSurface,
                            labelColor = NoxTextMuted
                        )
                    )
                }
            }

            // Input Code Box
            OutlinedTextField(
                value = codeInput,
                onValueChange = { codeInput = it },
                placeholder = { Text("// Paste source code, scripts or snippets here...", color = NoxTextMuted) },
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NoxBloodRed,
                    unfocusedBorderColor = NoxCardBorder,
                    focusedTextColor = NoxTextWhite,
                    unfocusedTextColor = NoxTextWhite,
                    focusedContainerColor = NoxCardSurface,
                    unfocusedContainerColor = NoxCardSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Button(
                onClick = { processCode() },
                enabled = codeInput.isNotBlank() && !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = NoxBloodRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NoxTextWhite)
                } else {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = NoxTextWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EXECUTE CODE ANALYSIS", color = NoxTextWhite, fontWeight = FontWeight.Bold)
                }
            }

            // Output Code Box
            Card(
                colors = CardDefaults.cardColors(containerColor = NoxCardSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NoxCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("OUTPUT / RESULT", color = NoxCrimson, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        IconButton(onClick = { clipboardManager.setText(AnnotatedString(codeOutput)) }) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = NoxTextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = codeOutput,
                            color = NoxTextWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
