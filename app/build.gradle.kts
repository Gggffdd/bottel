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
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
}
