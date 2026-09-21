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

tasks.register("checkReleaseGate") {
    group = "verification"
    description = "Refuses a public release until packaged content and retained evidence clear the release gate."
    dependsOn("testDebugUnitTest")
    doLast {
        val report = layout.buildDirectory.file("reports/release-gate.txt").get().asFile
        if (!report.isFile) {
            throw GradleException("The release gate did not write a report.")
        }
        val text = report.readText().trim()
        if (!text.startsWith("APPROVED")) {
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
