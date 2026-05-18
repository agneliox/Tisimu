import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.appdistribution)
    alias(libs.plugins.google.firebase.crashlytics)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

android {
    namespace = "com.lhavanguane.tisimu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lhavanguane.tisimu"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        named("main") {
            assets.srcDirs("src/main/assets")
        }
    }

    signingConfigs {
        create("release") {
            val signingProperties = Properties()
            val propertiesFile = file("signing.properties")
            if (propertiesFile.exists()) {
                signingProperties.load(propertiesFile.inputStream())
                storeFile = rootProject.file(signingProperties.getProperty("STORE_FILE_PATH"))
                storePassword = signingProperties.getProperty("STORE_PASSWORD")
                keyAlias = signingProperties.getProperty("KEY_ALIAS")
                keyPassword = signingProperties.getProperty("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            firebaseAppDistribution {
                serviceCredentialsFile = "$rootDir/tisimu-app-firebase-adminsdk-fbsvc-f3de8eaede.json"
                testers = "agnelio.lhavanguane@gmail.com, ndjanga@gmail.com"
                releaseNotes = "Debug build for internal testing"
            }
        }
        release {
            firebaseAppDistribution {
                serviceCredentialsFile = "$rootDir/tisimu-app-firebase-adminsdk-fbsvc-f3de8eaede.json"
                testers = "agnelio.lhavanguane@gmail.com, ndjanga@gmail.com"
                releaseNotes = "Release build"
            }

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        animationsDisabled = true
        // execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common.jvm)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ai)
    implementation(libs.room.runtime)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.swiperefreshlayout)
    implementation(libs.preference)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.room.compiler)
    // Add splash screen API
    implementation(libs.core.splashscreen)

    // For audio playback
    implementation(libs.exoplayer.core)
    implementation(libs.exoplayer.ui)
    // For image loading
    implementation(libs.glide)
    implementation(libs.gson)
    // Define a BOM to manage versions
    implementation(platform(libs.okhttp.bom))

    // Add the core OkHttp library
    implementation(libs.okhttp)

    // Navigation Component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.core.splashscreen.v120)

    implementation(libs.circleimageview)

// Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)

    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")

    // Kotlin Coroutines Testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Truth assertions
    testImplementation("com.google.truth:truth:1.1.5")
    androidTestImplementation("com.google.truth:truth:1.1.5")
}
