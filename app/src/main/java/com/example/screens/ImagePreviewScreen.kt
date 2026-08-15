package com.example.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    uri: Uri,
    onRetake: () -> Unit,
    onScan: () -> Unit
) {
    var enhancementMode by remember { mutableStateOf("Original") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview Document") },
                navigationIcon = {
                    IconButton(onClick = onRetake) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retake/Back")
                    }
                },
                actions = {
                    TextButton(onClick = onScan) {
                        Text("SCAN", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = onRetake) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Retake")
                    }
                    IconButton(onClick = { /* Rotate */ }) {
                        Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = "Rotate")
                    }
                    IconButton(onClick = { /* Crop */ }) {
                        Icon(Icons.Default.Crop, contentDescription = "Crop")
                    }
                    IconButton(onClick = { enhancementMode = "Enhanced" }) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = "Enhance")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Using Coil for image display
                val context = androidx.compose.ui.platform.LocalContext.current
                AsyncImage(
                    model = coil.request.ImageRequest.Builder(context)
                        .data(uri)
                        .allowHardware(false)
                        .build(),
                    contentDescription = "Document Preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Enhancement options mock
            ScrollableTabRow(
                selectedTabIndex = listOf("Original", "Grayscale", "High Contrast", "Sharpen").indexOf(enhancementMode).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Original", "Grayscale", "High Contrast", "Sharpen").forEachIndexed { index, mode ->
                    Tab(
                        selected = enhancementMode == mode,
                        onClick = { enhancementMode = mode },
                        text = { Text(mode) }
                    )
                }
            }
        }
    }
}
