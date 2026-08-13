package com.example.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanCamera: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onPdfSelected: (Uri) -> Unit,
    onDemoSelected: () -> Unit,
    onSettings: () -> Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let { onImageSelected(it) } }
    )
    
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let { onPdfSelected(it) } }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mahadev Bill Scanner") },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Scan or Upload Document",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Handwriting recognition may require manual correction.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HomeButton(
                text = "Scan with Camera",
                icon = Icons.Default.CameraAlt,
                onClick = onScanCamera
            )

            HomeButton(
                text = "Upload Image",
                icon = Icons.Default.Image,
                onClick = { imagePicker.launch("image/*") }
            )

            HomeButton(
                text = "Upload PDF",
                icon = Icons.Default.PictureAsPdf,
                onClick = { pdfPicker.launch("application/pdf") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onDemoSelected,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Load Demo Bill")
            }
        }
    }
}

@Composable
fun HomeButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp).padding(end = 12.dp))
        Text(text, fontSize = 18.sp)
    }
}
