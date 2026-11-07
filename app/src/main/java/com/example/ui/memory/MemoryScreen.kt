package com.example.ui.memory

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
import com.example.data.MemoryEntity
import com.example.data.NoxDatabase
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { NoxDatabase.getDatabase(context).noxDao() }

    val memories by dao.getAllMemories().collectAsState(initial = emptyList())
    var newMemoryInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NOX PERSONAL MEMORY", color = NoxTextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NoxTextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { scope.launch { dao.clearAllMemories() } }) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = NoxCrimson)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newMemoryInput,
                    onValueChange = { newMemoryInput = it },
                    placeholder = { Text("Store memory or preference...", color = NoxTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NoxBloodRed,
                        unfocusedBorderColor = NoxCardBorder,
                        focusedTextColor = NoxTextWhite,
                        unfocusedTextColor = NoxTextWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newMemoryInput.isNotBlank()) {
                            val content = newMemoryInput
                            newMemoryInput = ""
                            scope.launch {
                                dao.insertMemory(MemoryEntity(content = content))
                            }
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .background(NoxBloodRed, RoundedCornerShape(12.dp))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = NoxTextWhite)
                }
            }

            Text("SAVED NEURAL MEMORIES", color = NoxCrimson, fontWeight = FontWeight.Bold, fontSize = 12.sp)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(memories, key = { it.id }) { mem ->
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
                            Text(
                                text = mem.content,
                                color = NoxTextWhite,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { scope.launch { dao.deleteMemory(mem.id) } }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = NoxTextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}
