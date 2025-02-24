import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTask
import java.io.FileInputStream
import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.jetbrains.dokka)
    id("maven-publish")
    id("com.github.ben-manes.versions") version "0.46.0"
}

extra.apply {
    set("versionMajor", 0)
    set("versionMedium", 0)
    set("versionMinorPublished", 204) // should increment after public release
    set("libraryId", libraryId())
    set("libraryGroupId", libraryId())
    set("libraryArtifactId", libraryArtifactId())
    set("defaultRepo", "")
}

fun libraryArtifactId(): String {
    return if (project.hasProperty("LIB_ART_ID")) {
        project.property("LIB_ART_ID") as String
    } else {
        "core"
    }
}

fun libraryId(): String {
    return if (project.hasProperty("LIB_ID")) {
        project.property("LIB_ID") as String
    } else {
        "circle.modularwallets"
    }
}

fun buildNum(): Int {
    return if (project.hasProperty("BUILD_NUM")) {
        project.property("BUILD_NUM").toString().toInt()
    } else {
        0
    }
}

fun majorNumber(): Int {
    return if (project.hasProperty("MAJOR_NUMBER")) {
        project.property("MAJOR_NUMBER").toString().toInt()
    } else {
        extra["versionMajor"] as Int
    }
}

fun isInternalBuild(): Boolean {
    return majorNumber() == 0

}

fun apiVersion(): String {
    return if (isInternalBuild()) {
        "${majorNumber()}.${extra["versionMedium"]}.${extra["versionMinorPublished"]}-${buildNum()}" // internal build's buildNum is action build number
    } else {
        "${majorNumber()}.${extra["versionMedium"]}.${buildNum()}" // release build's buildNum is extracted from the git tag
    }
}

fun libraryVersion(): String {
    val ver = apiVersion()
    return if (project.hasProperty("SNAPSHOT")) {
        "${ver}-SNAPSHOT"
    } else {
        ver
    }
}

fun nexusRepo(): String {
    return if (project.hasProperty("NEXUS_REPO")) {
        project.property("NEXUS_REPO") as String
    } else {
        extra["defaultRepo"] as String
    }
}

fun nexusUsername(): String {
    return if (project.hasProperty("NEXUS_USERNAME")) {
        project.property("NEXUS_USERNAME") as String
    } else {
        ""
    }
}

fun nexusPassword(): String {
    return if (project.hasProperty("NEXUS_PASSWORD")) {
        project.property("NEXUS_PASSWORD") as String
    } else {
        ""
    }
}

android {
    namespace = "com.circle.modularwallets.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 28
        version = libraryVersion()
        buildConfigField("String", "version", "\"${version}\"")
        buildConfigField("boolean", "INTERNAL_BUILD", "${isInternalBuild()}")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += mapOf(
            "notPackage" to "com.circle.modularwallets.core.manual"
        )
    }

    buildTypes {
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    ksp(libs.moshi.ksp)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.auth)
    implementation(libs.retrofit2.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.web3j)
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.gson)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.web3j)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Add Mockito dependency
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.android)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)
}

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = extra["libraryGroupId"] as String
                artifactId = extra["libraryArtifactId"] as String
                version = libraryVersion()
            }
        }

        repositories {
            maven {
                name = "CircleModularwallets"
                url = URI(nexusRepo())
                isAllowInsecureProtocol = true
                credentials {
                    username = nexusUsername()
                    password = nexusPassword()
                }
            }
        }
    }
}