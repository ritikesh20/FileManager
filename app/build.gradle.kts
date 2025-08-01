plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.filemanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.filemanager"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.mikepenz:fastadapter:3.3.1")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.mikepenz:iconics-views:3.2.5")

    implementation("com.mikepenz:google-material-typeface:3.0.1.3.original@aar")
    implementation("com.mikepenz:community-material-typeface:2.7.94.1")

//    implementation ("com.mikepenz:iconics-core:5.3.5")
//    implementation ("com.mikepenz:google-material-typeface:3.0.1.5@aar")


    implementation("com.mikepenz:materialize:1.2.0@aar")

    implementation("com.davemorrissey.labs:subsampling-scale-image-view:3.10.0")

    implementation("androidx.drawerlayout:drawerlayout:1.2.0")

    implementation("androidx.documentfile:documentfile:1.0.1")

    implementation("com.google.android.material:material:1.12.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")

//    implementation ("com.airbnb.android:lottie:6.4.0")

    implementation("com.airbnb.android:lottie:6.0.0")

    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    implementation("androidx.preference:preference:1.2.1")


}