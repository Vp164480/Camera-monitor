import sys

with open("app/src/main/java/com/example/scanner/ScannerViewModel.kt", "r") as f:
    code = f.read()

old_update = """    fun updateItem(updatedItem: BillItem) {
        val current = currentBillResult ?: return
        val newItems = current.items.map { if (it.id == updatedItem.id) updatedItem else it }
        val newResult = current.copy(items = newItems)"""
new_update = """    fun updateItem(updatedItem: BillItem) {
        val current = currentBillResult ?: return
        val newItems = current.items.map { if (it.id == updatedItem.id) updatedItem else it }
        val newSubtotal = newItems.sumOf { it.amount }
        val newResult = current.copy(items = newItems, subtotal = newSubtotal)"""

old_delete = """    fun deleteItem(item: BillItem) {
        val current = currentBillResult ?: return
        val newItems = current.items.filter { it.id != item.id }
        val newResult = current.copy(items = newItems)"""
new_delete = """    fun deleteItem(item: BillItem) {
        val current = currentBillResult ?: return
        val newItems = current.items.filter { it.id != item.id }
        val newSubtotal = newItems.sumOf { it.amount }
        val newResult = current.copy(items = newItems, subtotal = newSubtotal)"""

old_add = """    fun addItem() {
        val current = currentBillResult ?: return
        val newItems = current.items.toMutableList()
        newItems.add(BillItem(productName = "New Item", quantity = 1, price = 0.0))
        val newResult = current.copy(items = newItems)"""
new_add = """    fun addItem() {
        val current = currentBillResult ?: return
        val newItems = current.items.toMutableList()
        newItems.add(BillItem(productName = "New Item", quantity = 1, price = 0.0))
        val newSubtotal = newItems.sumOf { it.amount }
        val newResult = current.copy(items = newItems, subtotal = newSubtotal)"""

code = code.replace(old_update, new_update)
code = code.replace(old_delete, new_delete)
code = code.replace(old_add, new_add)

with open("app/src/main/java/com/example/scanner/ScannerViewModel.kt", "w") as f:
    f.write(code)

