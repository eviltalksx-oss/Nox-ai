package com.example.ui.files

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.data.GeminiAIClient
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileUploadScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val geminiClient = remember { GeminiAIClient() }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("No file selected") }
    var analysisResult by remember { mutableStateOf("-- made by NOX --\nUpload a document, code file, or image to initiate neural analysis.") }
    var isAnalyzing by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            fileName = uri.lastPathSegment ?: "Attached File"
        }
    }

    fun analyzeFile() {
        if (selectedFileUri == null || isAnalyzing) return
        isAnalyzing = true
        scope.launch {
            val prompt = "Analyze attached file content ($fileName), extract key insights, summarize structure, and report anomalies."
            val result = geminiClient.generateResponse(prompt, "You are NOX File Intelligence. Provide deep technical and structural analysis.")
            analysisResult = result
            isAnalyzing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NOX FILE INTELLIGENCE", color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dropzone card
            Card(
                onClick = { filePickerLauncher.launch("*/*") },
                colors = CardDefaults.cardColors(containerColor = NoxCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NoxCrimson),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = NoxCrimson, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = fileName, color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Tap to select PDF, TXT, MD, Code or Image", color = NoxTextMuted, fontSize = 12.sp)
                }
            }

            Button(
                onClick = { analyzeFile() },
                enabled = selectedFileUri != null && !isAnalyzing,
                colors = ButtonDefaults.buttonColors(containerColor = NoxBloodRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NoxTextWhite)
                } else {
                    Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = NoxTextWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("START NEURAL FILE ANALYSIS", color = NoxTextWhite, fontWeight = FontWeight.Bold)
                }
            }

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
                    Text("ANALYSIS REPORT", color = NoxCrimson, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = analysisResult,
                            color = NoxTextWhite,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
