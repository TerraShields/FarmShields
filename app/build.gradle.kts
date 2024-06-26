plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevtoolsKsp)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("kotlin-android")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

android {
    namespace = "com.gagak.farmshields"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gagak.farmshields"
        minSdk = 21
        targetSdk = 34
        versionCode = 2
        versionName = "2.3.1"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://api-service-backend-tpercgplna-uc.a.run.app/api/\"")
        buildConfigField("String", "BASE_URL_CHAT", "\"https://chat-api-tpercgplna-et.a.run.app/\"")
        buildConfigField("String", "BASE_URL_REPORT", "\"https://api-model-tpercgplna-et.a.run.app/api/\"")
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // viewpager
    implementation(libs.androidx.viewpager2)
    implementation(libs.dotsindicator)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Room
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.paging)

    // Camera
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Paging3
    implementation(libs.androidx.paging.runtime.ktx)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // DataStore
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Glide & Image Loading
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation (libs.shimmer)
    implementation(libs.androidx.cardview)

    // Google Service
    implementation(libs.androidx.credentials)
    // optional - needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.play.services.auth)
    implementation(libs.googleid)


    // Splash screen
    implementation (libs.androidx.core.splashscreen)

    // Image layout
    implementation (libs.circleimageview)

    //
    implementation (libs.lottie)
}