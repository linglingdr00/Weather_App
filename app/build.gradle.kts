plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/tinatang/Documents/app/weather/release/release.jks")
            storePassword = "702424"
            keyAlias = "weather"
            keyPassword = "702424"
        }
    }
    namespace = "com.linglingdr00.weather"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.linglingdr00.weather"
        minSdk = 26
        targetSdk = 33
        versionCode = 2
        versionName = "1.1"

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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

/* fix kaptGenerateStubsKotlin bug */
tasks.withType(type = org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask::class) {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Moshi
    implementation ("com.squareup.moshi:moshi-kotlin:1.13.0")
    // Retrofit with Moshi Converter
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    implementation ("com.google.android.gms:play-services-location:19.0.1")

    // Room
    //implementation ("androidx.room:room-runtime:2.4.3")
    //kapt ("androidx.room:room-compiler:2.4.3")
    //implementation ("androidx.room:room-ktx:2.4.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}