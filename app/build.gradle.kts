plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("com.google.gms.google-services")
}

android {
    namespace = "kr.blugon.tjfinder"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.blugon.tjfinder"
        minSdk = 28
        targetSdk = 34
        versionCode = 19
        versionName = "beta_v1.7.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/CONTRIBUTORS.md"
            excludes += "/META-INF/LICENSE.md"
        }
    }
}

dependencies {
    implementation(libs.coreKtx)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.activityCompose)
    implementation(platform(libs.composeBom))
    androidTestImplementation(platform(libs.composeBom))
    implementation(libs.ui)
    implementation(libs.uiGraphics)
    implementation(libs.uiToolingPreview)
    debugImplementation(libs.uiTooling)
    debugImplementation(libs.uiTestManifest)
    implementation(libs.material3)
    implementation(libs.navigationCompose)
    implementation(libs.json)
    implementation(libs.securityCryptoKtx)
    implementation("${libs.securityCryptoKtx.get().group}:${libs.securityCryptoKtx.get().name}:1.1.0-alpha06")
    testImplementation(libs.junit)
    androidTestImplementation(libs.extJunit)
    androidTestImplementation(libs.uiTestJunit4)
    androidTestImplementation(libs.espressoCore)

    implementation(libs.fuel)

    implementation(libs.firebaseAuth)
    implementation(libs.playServicesAuth)
    implementation("com.google.gms:google-services:4.4.2")

    implementation(libs.coilCompose)

    implementation(libs.lazycolumnscrollbar)
    implementation(libs.accompanistSystemuicontroller)

    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converterGson)
    implementation(libs.moshi)
//    implementation("com.google.gms:google-services:4.4.2")
//    implementation("com.google.android.gms:play-services-ads:23.1.0")

    implementation(libs.sqlite)
//    implementation(libs.sqliteBundled)

    implementation(libs.room)
//    implementation(libs.roomRuntime)
//    implementation(libs.roomCompiler)

    implementation(libs.jsoup)
    implementation(libs.kuromoji)
}

apply(plugin = "com.google.gms.google-services")