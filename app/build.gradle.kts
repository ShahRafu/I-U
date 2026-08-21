plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.Pikachu.owner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.Pikachu.owner"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // NDK & C++ Architecture Filter
        ndk {
            abiFilters.addAll(setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    // 🔻 C++ (CMake) বিল্ড কনফিগারেশন 🔻
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.18.1"
        }
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
}

dependencies {
    // AndroidX & UI Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Kotlin Coroutines (ব্যাকগ্রাউন্ড থ্রেডিং ও মিলি-সেকেন্ড অটো-ট্যাপ ম্যানেজমেন্টের জন্য)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Google ML Kit Text Recognition (অন-ডিভাইস OCR & টাইমার রিডার)
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
