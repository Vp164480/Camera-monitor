import sys

with open("app/src/main/java/com/example/screens/ReviewScreen.kt", "r") as f:
    code = f.read()

# 1. Update signature
old_sig = """fun ReviewScreen(
    result: BillResult,
    viewModel: ScannerViewModel,
    onClose: () -> Unit
) {"""

new_sig = """import android.net.Uri
fun ReviewScreen(
    result: BillResult,
    originalUris: List<Uri>,
    viewModel: ScannerViewModel,
    onClose: () -> Unit,
    onRescan: () -> Unit
) {"""
code = code.replace(old_sig, new_sig)

# 2. Add customer fields
old_fields = """    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }"""

new_fields = """    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }"""
code = code.replace(old_fields, new_fields)

# 3. Change Save to Create Customer Bill
code = code.replace("Text(\"Save Bill (₹${result.calculatedTotal})\")", "Text(\"Create Customer Bill (₹${result.calculatedTotal})\")")

# 4. Remove first Customer Info block
first_customer_block = """                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Customer Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(16.dp))"""
code = code.replace(first_customer_block, "                    Spacer(modifier = Modifier.height(8.dp))")

# 5. Expand bottom Customer Info block and add original doc
second_customer_block = """                    Text("Customer Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(16.dp))"""

expanded_customer_block = """                    Text("Customer Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Mobile") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it },
                        label = { Text("Vehicle Number") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = vehicleModel,
                        onValueChange = { vehicleModel = it },
                        label = { Text("Vehicle Model") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Original Document", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (originalUris.isNotEmpty()) {
                            OutlinedButton(onClick = { /* View Logic */ }, modifier = Modifier.weight(1f)) {
                                Text("👁️ View")
                            }
                            OutlinedButton(onClick = { viewModel.reset() }, modifier = Modifier.weight(1f)) {
                                Text("🗑️ Remove")
                            }
                        }
                        OutlinedButton(onClick = onRescan, modifier = Modifier.weight(1f)) {
                            Text("🔄 Rescan")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))"""

code = code.replace(second_customer_block, expanded_customer_block)

with open("app/src/main/java/com/example/screens/ReviewScreen.kt", "w") as f:
    f.write(code)

