import sys

with open("app/src/main/java/com/example/screens/ReviewScreen.kt", "r") as f:
    code = f.read()

old_state = """    var showRawText by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }"""

new_state = """    var showRawText by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var showOriginalImage by remember { mutableStateOf(false) }"""
code = code.replace(old_state, new_state)

old_btn = "OutlinedButton(onClick = { /* View Logic */ }, modifier = Modifier.weight(1f))"
new_btn = "OutlinedButton(onClick = { showOriginalImage = true }, modifier = Modifier.weight(1f))"
code = code.replace(old_btn, new_btn)

dialog_code = """
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
"""
code = code.replace("}\n\n@Composable\nfun BillItemRow(", dialog_code)

with open("app/src/main/java/com/example/screens/ReviewScreen.kt", "w") as f:
    f.write(code)

