plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.lemoncoin"
    compileSdk = 35
    viewBinding{
        enable = true
    }

    defaultConfig {
        applicationId = "com.example.lemoncoin"
        minSdk = 26 //nota: antes estava 24,
        // 26 não permite que o app funcione em android abaixo de 7.1 (10% dos brasileiros)
        //coloquei em 26 para funcionar o exportar excel
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.ui.text.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // https://mvnrepository.com/artifact/com.google.android.material/material
    // Substituir a versão antiga do design (com.android.support) pela versão androidx
    implementation("com.google.android.material:material:1.13.0-alpha11") // Material Design do Google
    implementation("androidx.appcompat:appcompat:1.4.1") // Substituindo o androidx.appcompat
    implementation("androidx.legacy:legacy-support-v4:1.0.0") // Substituindo o support-v4
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    //https://github.com/santalu/maskara
    implementation("com.github.santalu:maskara:1.0.0")
    //gerar excel
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
}