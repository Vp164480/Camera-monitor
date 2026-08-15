package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.BillItem
import com.example.models.BillResult
import com.example.scanner.ScannerViewModel
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    result: BillResult,
    originalUris: List<Uri>,
    viewModel: ScannerViewModel,
    onClose: () -> Unit,
    onRescan: () -> Unit
) {
    var showRawText by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var showOriginalImage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bill Preview") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addItem() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = { /* In real app, passes data back */ },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Create Customer Bill (₹${result.calculatedTotal})")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("MAHADEV AUTO GARAGE", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("OCR Mode: ${result.source}", style = MaterialTheme.typography.bodySmall)
                    
                    if (result.rawText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Raw OCR Text", fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { showRawText = !showRawText }) {
                                        Icon(
                                            if (showRawText) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = "Toggle Raw Text"
                                        )
                                    }
                                }
                                if (showRawText) {
                                    Text(
                                        text = result.rawText,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Product", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                        Text("Qty", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text("Price", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(48.dp)) // For delete icon
                    }
                }

                items(result.items, key = { it.id }) { item ->
                    BillItemRow(
                        item = item,
                        onUpdate = { viewModel.updateItem(it) },
                        onDelete = { viewModel.deleteItem(it) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal:", fontWeight = FontWeight.Bold)
                        Text("₹${result.subtotal}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Discount:", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = if (result.discount == 0.0) "" else result.discount.toString(),
                            onValueChange = { 
                                val d = it.toDoubleOrNull() ?: 0.0
                                viewModel.updateDiscount(d)
                            },
                            modifier = Modifier.width(100.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            prefix = { Text("₹") }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Grand Total:", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("₹${result.calculatedTotal}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                    
                    if (result.detectedTotal != null && result.detectedTotal != result.calculatedTotal) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "⚠️ Total mismatch — detected: ₹${result.detectedTotal}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if (showOriginalImage && originalUris.isNotEmpty()) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showOriginalImage = false }) {
            Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha=0.8f)).padding(16.dp)) {
                coil.compose.AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(originalUris.first())
                        .allowHardware(false)
                        .build(),
                    contentDescription = "Original",
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(onClick = { showOriginalImage = false }, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }
}
@Composable
fun BillItemRow(

    item: BillItem,
    onUpdate: (BillItem) -> Unit,
    onDelete: (BillItem) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = item.productName,
                onValueChange = { onUpdate(item.copy(productName = it)) },
                modifier = Modifier.weight(2f),
                singleLine = true
            )
            
            OutlinedTextField(
                value = item.quantity.toString(),
                onValueChange = { 
                    val q = it.toIntOrNull() ?: 0
                    onUpdate(item.copy(quantity = q)) 
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            OutlinedTextField(
                value = item.price.toString(),
                onValueChange = {
                    val p = it.toDoubleOrNull() ?: 0.0
                    onUpdate(item.copy(price = p))
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            IconButton(onClick = { onDelete(item) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Item", tint = MaterialTheme.colorScheme.error)
            }
        }
        if (item.confidence != null && item.confidence!! < 0.8f) {
            Text("⚠️ Low confidence (${(item.confidence!! * 100).toInt()}%) - Please verify", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ProcessingScreen(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text, fontSize = 18.sp)
        }
    }
}

@Composable
fun ErrorScreen(message: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("Error", color = MaterialTheme.colorScheme.error, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onDismiss) {
                Text("Go Back")
            }
        }
    }
}

@Composable
fun ConfirmOnlineDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Use Online OCR?") },
        text = { Text("Online OCR will upload this document for processing.\n\nContinue?") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Continue") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}
