package com.example.ui.device

import android.Manifest
import android.content.Intent
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceIntegrationScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var actionLog by remember { mutableStateOf(listOf("NOX Device Subsystem Initialized.")) }

    val locLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        actionLog = actionLog + if (granted) "Location permission granted." else "Location permission denied."
    }

    val camLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        actionLog = actionLog + if (granted) "Camera permission granted." else "Camera permission denied."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DEVICE INTEGRATIONS", color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            Text(
                text = "SUPPORTED HARDWARE SUBSYSTEMS",
                color = NoxCrimson,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    IntegrationCard(
                        title = "Location Services",
                        desc = "Acquire precise GPS telemetry and geofencing.",
                        icon = Icons.Default.LocationOn,
                        onExecute = { locLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                    )
                }
                item {
                    IntegrationCard(
                        title = "Camera Capture",
                        desc = "Initialize optical sensor for visual inspection.",
                        icon = Icons.Default.CameraAlt,
                        onExecute = { camLauncher.launch(Manifest.permission.CAMERA) }
                    )
                }
                item {
                    IntegrationCard(
                        title = "Calendar Event Sync",
                        desc = "Query device calendar schedules and add reminders.",
                        icon = Icons.Default.CalendarToday,
                        onExecute = {
                            val intent = Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE, "NOX Reminder")
                            try { context.startActivity(intent) } catch (e: Exception) {}
                            actionLog = actionLog + "Opened Calendar Intent."
                        }
                    )
                }
                item {
                    IntegrationCard(
                        title = "Contacts Index",
                        desc = "Search and reference phone contact database.",
                        icon = Icons.Default.Contacts,
                        onExecute = {
                            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                            try { context.startActivity(intent) } catch (e: Exception) {}
                            actionLog = actionLog + "Opened Contacts Intent."
                        }
                    )
                }
            }

            // Action Execution Log
            Card(
                colors = CardDefaults.cardColors(containerColor = NoxCardSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NoxCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("EXECUTION TELEMETRY LOG", color = NoxTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(actionLog) { log ->
                            Text(text = "• $log", color = NoxTextWhite, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IntegrationCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onExecute: () -> Unit) {
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(imageVector = icon, contentDescription = null, tint = NoxCrimson, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = title, color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = desc, color = NoxTextMuted, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onExecute,
                colors = ButtonDefaults.buttonColors(containerColor = NoxBloodRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Execute", fontSize = 12.sp)
            }
        }
    }
}
