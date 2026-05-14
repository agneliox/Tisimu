import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.appdistribution)
}

android {
    namespace = "com.lhavanguane.tisimu"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.lhavanguane.tisimu"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            firebaseAppDistribution {
                serviceCredentialsFile="$rootDir/tisimu-app-firebase-adminsdk-fbsvc-f3de8eaede.json"
                testers="tester1@example.com, tester2@example.com"
                releaseNotes="Debug build for internal testing"
            }
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    implementation("androidx.core:core-splashscreen:1.2.0")
}