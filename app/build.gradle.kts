plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.russiancompanion"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "dev.russiancompanion"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.0"
        val gitCommit = providers.environmentVariable("GITHUB_SHA").orElse("local")
        buildConfigField("String", "GIT_COMMIT", "\"${gitCommit.get()}\"")
        buildConfigField("String", "VERSION_NAME", "\"1.0.0\"")
        buildConfigField("int", "VERSION_CODE", "2")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
        disable += setOf("GradleDependency", "OldTargetApi", "AndroidGradlePluginVersion")
    }
}

tasks.register<JavaExec>("checkReleaseGate") {
    group = "verification"
    description = "Refuses a public release until the packaged catalog and retained-evidence manifest clear the release gate."
    val compileKotlin = tasks.named("compileDebugKotlin")
    dependsOn(compileKotlin)
    workingDir = rootProject.layout.projectDirectory.asFile
    mainClass.set("dev.russiancompanion.release.ReleaseGateKt")
    classpath(compileKotlin.map { it.outputs.files }, configurations.named("debugRuntimeClasspath"))
    isIgnoreExitValue = true
    doLast {
        val exitValue = executionResult.get().exitValue
        val report = rootProject.file("app/build/reports/release-gate.txt")
        if (!report.isFile) {
            throw GradleException("The release gate did not write a report.")
        }
        val text = report.readText().trim()
        if (exitValue != 0 || !text.startsWith("APPROVED")) {
            throw GradleException("Public release is not approved.\n$text")
        }
    }
}

fun isReleaseOutputTask(name: String): Boolean {
    if (name.isEmpty()) return false
    if (name.contains("Test") || name.contains("Lint") || name.startsWith("lint")) return false
    if (name.contains("checkReleaseGate")) return false
    if (!name.contains("Release")) return false
    return when {
        name.startsWith("assemble") -> true
        name.startsWith("bundle") -> !name.contains("Classes") && !name.contains("Lib")
        name.startsWith("publish") -> true
        name.startsWith("install") -> true
        name.startsWith("sign") -> true
        name.startsWith("package") -> {
            name == "packageRelease" ||
                name.endsWith("Release") ||
                name.endsWith("ReleaseBundle") ||
                name.endsWith("ReleaseApk") ||
                name.endsWith("ReleaseAab") ||
                name.endsWith("ReleaseUniversalApk")
        }
        else -> false
    }
}

tasks.configureEach {
    if (isReleaseOutputTask(name)) {
        dependsOn("checkReleaseGate")
    }
}

tasks.register("verifyReleaseGateAttachment") {
    group = "verification"
    description = "Proves every realized release APK/AAB/publish task depends on checkReleaseGate."
    doLast {
        val required = listOf(
            "assembleRelease",
            "packageRelease",
            "bundleRelease",
            "packageReleaseBundle",
        )
        for (taskName in required) {
            val task = tasks.findByName(taskName) ?: tasks.named(taskName).get()
            val deps = task.taskDependencies.getDependencies(task).map { it.name }
            if ("checkReleaseGate" !in deps) {
                throw GradleException("$taskName is not fail-closed through checkReleaseGate. deps=$deps")
            }
        }
        val ungated = tasks.filter { isReleaseOutputTask(it.name) }.filter { task ->
            "checkReleaseGate" !in task.taskDependencies.getDependencies(task).map { it.name }
        }
        if (ungated.isNotEmpty()) {
            throw GradleException(
                "Release output tasks are not gated: ${ungated.joinToString { it.name }}",
            )
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.room.compiler)
    testImplementation(composeBom)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}
