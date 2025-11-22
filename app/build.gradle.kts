plugins {
    id("com.android.application")
    id("com.chaquo.python")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.micronimbus.doctorshaheb"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.micronimbus.doctorshaheb"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
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

        sourceCompatibility= JavaVersion.VERSION_1_8
        targetCompatibility =JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

chaquopy {
    defaultConfig {
        buildPython("C:/path/to/python.exe")
        buildPython("C:/path/to/py.exe", "-3.8")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.fragment)
    implementation(libs.legacy.support.v4)
    implementation(libs.volley)
    implementation(libs.firebase.firestore)
    implementation(libs.generativeai)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide)
    annotationProcessor(libs.glideCompiler)
    implementation(libs.firebase.storage)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.hbb20:ccp:2.7.3")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("androidx.cardview:cardview:1.0.0")




// UI
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")


    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.4.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.4.1")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.4.1")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.4.1")
    implementation("com.google.ai.client.generativeai:common:0.7.0")




    dependencies {
        implementation("io.github.jan-tennert.supabase:gotrue-kt:2.4.3")
        implementation("io.github.jan-tennert.supabase:storage-kt:2.4.3")
        implementation("io.github.jan-tennert.supabase:postgrest-kt:2.4.3")
    }



}
