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
        versionCode = 1
        versionName = "0.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
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

tasks.configureEach {
    if (name == "assembleRelease" || name == "packageRelease") {
        dependsOn("checkReleaseGate")
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
