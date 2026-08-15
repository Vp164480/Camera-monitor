import sys

with open("app/src/main/java/com/example/screens/ReviewScreen.kt", "r") as f:
    code = f.read()

code = code.replace("import com.example.scanner.ScannerViewModel", "import com.example.scanner.ScannerViewModel\nimport android.net.Uri")
code = code.replace("@Composable\nimport android.net.Uri\nfun ReviewScreen", "@Composable\nfun ReviewScreen")

with open("app/src/main/java/com/example/screens/ReviewScreen.kt", "w") as f:
    f.write(code)

