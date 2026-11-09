package dev.russiancompanion.testing

import java.util.concurrent.TimeUnit

/** Probe a real Python 3 interpreter, avoiding Windows' nonfunctional Store alias. */
internal fun selectPythonLauncher(candidates: List<List<String>>, works: (List<String>) -> Boolean): List<String> =
    candidates.firstOrNull(works) ?: error("No working Python 3.10+ interpreter. Install Python or set RC_TEST_PYTHON to its executable path.")

private val pythonLauncher: List<String> by lazy {
    val explicit = System.getenv("RC_TEST_PYTHON")?.takeIf { it.isNotBlank() }
    val candidates = if (explicit != null) listOf(listOf(explicit))
        else if (System.getProperty("os.name").startsWith("Windows")) listOf(listOf("py", "-3"), listOf("python"), listOf("python3"))
        else listOf(listOf("python3"), listOf("python"))
    selectPythonLauncher(candidates) { command ->
        try {
            val process = ProcessBuilder(command + listOf("-c", "import sys; assert sys.version_info >= (3,10); print('RC_PYTHON_3_OK')"))
                .redirectErrorStream(true).start()
            try {
                if (!process.waitFor(10, TimeUnit.SECONDS)) false
                else process.exitValue() == 0 && process.inputStream.bufferedReader().readText().trim() == "RC_PYTHON_3_OK"
            } finally {
                if (process.isAlive) process.destroyForcibly()
                process.inputStream.close()
                process.outputStream.close()
                process.errorStream.close()
            }
        } catch (_: java.io.IOException) {
            false
        }
    }
}

internal fun pythonProcess(vararg arguments: String): ProcessBuilder =
    ProcessBuilder(pythonLauncher + arguments).apply {
        environment()["PYTHONUTF8"] = "1"
        environment()["PYTHONIOENCODING"] = "utf-8"
    }
