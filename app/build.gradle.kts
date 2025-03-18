plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.lemoncoin"
    compileSdk = 35
    viewBinding{
        enable = true
    }

    defaultConfig {
        applicationId = "com.example.lemoncoin"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // https://mvnrepository.com/artifact/com.google.android.material/material
    // Substituir a versão antiga do design (com.android.support) pela versão androidx
    implementation("com.google.android.material:material:1.13.0-alpha11") // Material Design do Google
    implementation("androidx.appcompat:appcompat:1.4.1") // Substituindo o androidx.appcompat
    implementation("androidx.legacy:legacy-support-v4:1.0.0") // Substituindo o support-v4


}