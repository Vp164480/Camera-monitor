import sys

with open('app/src/main/java/com/example/screens/ReviewScreen.kt', 'r') as f:
    content = f.read()

# Replace BillItemRow
old_row = """@Composable
fun BillItemRow(
    item: BillItem,
    onUpdate: (BillItem) -> Unit,
    onDelete: (BillItem) -> Unit
) {
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
}"""

new_row = """@Composable
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
}"""

content = content.replace(old_row, new_row)

old_state = "var showRawText by remember { mutableStateOf(false) }"
new_state = """var showRawText by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }"""

content = content.replace(old_state, new_state)

old_info = "Spacer(modifier = Modifier.height(16.dp))\n                    \n                    Row("
new_info = """Spacer(modifier = Modifier.height(16.dp))
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
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row("""

content = content.replace(old_info, new_info)

with open('app/src/main/java/com/example/screens/ReviewScreen.kt', 'w') as f:
    f.write(content)

