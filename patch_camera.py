import sys

with open("app/src/main/java/com/example/screens/CameraScreen.kt", "r") as f:
    code = f.read()

# Fix padding for Button
old_btn = """            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp),"""
new_btn = """            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(bottom = 32.dp)
                .size(72.dp),"""
code = code.replace(old_btn, new_btn)

# Fix LocalLifecycleOwner deprecation
old_import = "import androidx.compose.ui.platform.LocalLifecycleOwner"
new_import = "import androidx.lifecycle.compose.LocalLifecycleOwner"
code = code.replace(old_import, new_import)

with open("app/src/main/java/com/example/screens/CameraScreen.kt", "w") as f:
    f.write(code)

