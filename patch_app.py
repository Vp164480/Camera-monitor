with open("app/src/main/java/com/example/screens/ScannerApp.kt", "r") as f:
    code = f.read()

old_call = """        is ScannerState.Review -> ReviewScreen(
            result = currentState.billResult,
            viewModel = viewModel,
            onClose = { viewModel.reset() }
        )"""

new_call = """        is ScannerState.Review -> ReviewScreen(
            result = currentState.billResult,
            originalUris = currentState.originalUris,
            viewModel = viewModel,
            onClose = { viewModel.reset() },
            onRescan = { viewModel.reset() }
        )"""

code = code.replace(old_call, new_call)
with open("app/src/main/java/com/example/screens/ScannerApp.kt", "w") as f:
    f.write(code)
