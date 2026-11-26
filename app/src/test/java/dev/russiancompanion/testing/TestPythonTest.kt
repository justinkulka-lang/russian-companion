package dev.russiancompanion.testing

import org.junit.Assert.*
import org.junit.Test

class TestPythonTest {
    @Test fun invalidStoreAliasIsRejectedAndLauncherArgumentsArePreserved() {
        val visited = mutableListOf<List<String>>()
        val real = listOf("C:/Program Files/Python/python.exe")
        val result = selectPythonLauncher(listOf(listOf("python3"), listOf("py", "-3"), real)) {
            visited += it
            it == real
        }
        assertEquals(real, result)
        assertEquals(listOf(listOf("python3"), listOf("py", "-3"), real), visited)
        assertEquals(listOf("py", "-3"), selectPythonLauncher(listOf(listOf("py", "-3"))) { true })
    }

    @Test fun noRealInterpreterFailsClearlyInsteadOfSilentlySkippingTheCheck() {
        try {
            selectPythonLauncher(listOf(listOf("python3"))) { false }
            fail("Missing Python accepted")
        } catch (expected: IllegalStateException) {
            assertTrue(expected.message.orEmpty().contains("RC_TEST_PYTHON"))
        }
    }

    @Test fun selectedInterpreterReallyRunsPythonWithUtf8OutputAndUnsplitArguments() {
        val process = pythonProcess("-c", "import sys; assert sys.version_info >= (3,10); print(sys.argv[1])", "English argument with spaces")
            .redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        assertEquals(output, 0, process.waitFor())
        assertEquals("English argument with spaces", output.trim())
    }
}
