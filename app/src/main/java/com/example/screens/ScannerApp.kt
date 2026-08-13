package com.example.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scanner.ScannerState
import com.example.scanner.ScannerViewModel

@Composable
fun ScannerApp(viewModel: ScannerViewModel = viewModel()) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onScanCamera = { navController.navigate("camera") },
                onImageSelected = { uri -> viewModel.processImage(context, uri) },
                onPdfSelected = { uri -> viewModel.processPdf(context, uri) },
                onDemoSelected = { viewModel.processDemoBill() },
                onSettings = { navController.navigate("settings") }
            )
        }
        
        composable("camera") {
            CameraScreen(
                onImageCaptured = { uri -> 
                    navController.popBackStack()
                    viewModel.processImage(context, uri)
                },
                onCancel = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
    
    // Overlay for processing / review / error state
    when (val currentState = state) {
        is ScannerState.Processing -> ProcessingScreen(currentState.progressText)
        is ScannerState.Review -> ReviewScreen(
            result = currentState.billResult,
            viewModel = viewModel,
            onClose = { viewModel.reset() }
        )
        is ScannerState.Error -> ErrorScreen(
            message = currentState.message,
            onDismiss = { viewModel.reset() }
        )
        else -> {}
    }
}
