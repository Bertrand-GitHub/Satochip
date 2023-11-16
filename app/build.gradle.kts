plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.relay") version "0.3.09"
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "fr.toporin.satochip"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.toporin.satochip"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // XML-RCP
    implementation("fr.turri:aXMLRPC:1.13.0")

    // BouncyCastle for Security
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.squareup.okio:okio:3.6.0")

    // For Java Crypto Tools
    implementation("com.google.guava:guava:32.1.3-jre")

    // Kotlin Core & AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("com.google.firebase:firebase-encoders-json:18.0.1")
    implementation("javax.inject:javax.inject:1")

    // Jetpack Compose UI
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("androidx.compose.runtime:runtime-rxjava3:1.5.4")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("com.google.android.material:material:1.10.0")

    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // CameraX pour l'aperçu de la caméra
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")


    // Java Crypto Tools
    implementation(files("src/main/java/fr/toporin/satochip/libs/javacryptotools-0.1.0.jar"))
    implementation("androidx.camera:camera-mlkit-vision:1.4.0-alpha02")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")

    // Android UI Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debugging & Tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
