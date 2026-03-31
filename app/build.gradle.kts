plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.messenger"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.messenger"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // Bottom navigation
    implementation("com.google.android.material:material:1.10.0")
    
    // Circle ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
