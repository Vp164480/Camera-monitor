package com.example.screens

import android.net.Uri
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
                onImageSelected = { uri -> 
                    navController.navigate("imagePreview/${Uri.encode(uri.toString())}") 
                },
                onPdfSelected = { uri -> viewModel.handleIncomingDocument(context, uri, isPdf = true) },
                onDemoSelected = { viewModel.processDemoBill() },
                onSettings = { navController.navigate("settings") }
            )
        }
        
        composable("camera") {
            CameraScreen(
                onImageCaptured = { uri -> 
                    navController.popBackStack()
                    navController.navigate("imagePreview/${Uri.encode(uri.toString())}")
                },
                onCancel = { navController.popBackStack() }
            )
        }
        
        composable("imagePreview/{uriEncoded}") { backStackEntry ->
            val uri = Uri.parse(Uri.decode(backStackEntry.arguments?.getString("uriEncoded") ?: ""))
            ImagePreviewScreen(
                uri = uri,
                onRetake = { navController.popBackStack() },
                onScan = { 
                    navController.popBackStack()
                    viewModel.handleIncomingDocument(context, uri, isPdf = false) 
                }
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
        is ScannerState.ConfirmOnlineUpload -> ConfirmOnlineDialog(
            onConfirm = { viewModel.confirmOnlineUpload(context, currentState.uri, currentState.isPdf, approved = true) },
            onCancel = { viewModel.confirmOnlineUpload(context, currentState.uri, currentState.isPdf, approved = false) }
        )
        is ScannerState.Processing -> ProcessingScreen(currentState.progressText)
        is ScannerState.Review -> ReviewScreen(
            result = currentState.billResult,
            originalUris = currentState.originalUris,
            viewModel = viewModel,
            onClose = { viewModel.reset() },
            onRescan = { viewModel.reset() }
        )
        is ScannerState.Error -> ErrorScreen(
            message = currentState.message,
            onDismiss = { viewModel.reset() }
        )
        else -> {}
    }
}
