plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "edu.cit.rivero.workspace"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.cit.rivero.workspace"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.junit.ktx)
    dependencies {
        // Force AndroidX Core to stay on API 34 compatible versions
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.core:core:1.13.1")

        // UI libraries (make sure these aren't pulling in newer stuff)
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")

        // Your Retrofit libraries you added earlier
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        // RecyclerView for your Dashboard
        implementation("androidx.recyclerview:recyclerview:1.3.2")
    }
}