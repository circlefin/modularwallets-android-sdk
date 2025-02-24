// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.ksp) apply false
}

subprojects {
    afterEvaluate {
        // Load configuration file (if it exists)
        val jacocoFile = file("../jacoco.gradle")
        if (jacocoFile.exists()) {
            apply(from = jacocoFile)
            println("jacoco applied.")
        } else {
            println("jacoco file not found.")
        }
    }
}