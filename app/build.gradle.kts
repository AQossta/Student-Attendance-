plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.studentattendanceproject2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.studentattendanceproject2"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val nav_version = "2.8.8"
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    implementation ("com.tbuonomo:dotsindicator:4.2")
    implementation ("com.github.bumptech.glide:glide:4.12.0")


    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.0.0-alpha27")
    implementation ("com.google.zxing:core:3.5.1")

    // Coroutines to make the HTTP requests asynchronous(In the background thread)
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // Okhttp3 for the POST requests
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.1")

    // Gson (To convert raw JSON to pretty JSON)
    implementation ("com.google.code.gson:gson:2.9.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")

    implementation ("androidx.camera:camera-core:1.3.2")
    implementation ("androidx.camera:camera-camera2:1.3.2")
    implementation ("androidx.camera:camera-lifecycle:1.3.2")
    implementation ("androidx.camera:camera-view:1.3.2")

    implementation ("com.google.mlkit:barcode-scanning:17.2.0")

    implementation ("com.google.zxing:core:3.5.2")
}