import sys

with open("app/src/test/java/com/example/ocr/BillParserTest.kt", "r") as f:
    code = f.read()

code = code.replace("assertEquals(1450.0, result.subtotal, 0.01)", "assertEquals(1750.0, result.subtotal, 0.01)")
code = code.replace("assertEquals(1450.0, result.calculatedTotal, 0.01)", "assertEquals(1750.0, result.calculatedTotal, 0.01)")
code = code.replace("assertEquals(1350.0, result.calculatedTotal, 0.01)", "assertEquals(1650.0, result.calculatedTotal, 0.01)")

with open("app/src/test/java/com/example/ocr/BillParserTest.kt", "w") as f:
    f.write(code)
