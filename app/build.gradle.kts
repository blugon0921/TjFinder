import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
//    id("com.google.devtools.ksp")
}
//apply(plugin = "com.google.gms.google-services")

android {
    namespace = "kr.blugon.tjfinder"
    compileSdk = 36

    defaultConfig {
        applicationId = "kr.blugon.tjfinder"
        minSdk = 33
        targetSdk = 35
        versionCode = 25
        versionName = "beta_v1.7.7"

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
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
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    implementation(libs.compose.navigation)
    implementation(libs.compose.icons)
//    implementation(libs.compose.icons.extended)
    implementation(libs.ui)
    implementation(libs.uiGraphics)
    implementation(libs.uiToolingPreview)
    debugImplementation(libs.uiTooling)
    debugImplementation(libs.uiTestManifest)
    implementation(libs.material3)
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
    implementation("com.google.gms:google-services:4.4.4")

    implementation(libs.coilCompose)

    implementation(libs.lazycolumnscrollbar)
    implementation(libs.accompanistSystemuicontroller)

    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converterGson)
    implementation(libs.moshi)
//    implementation("com.google.android.gms:play-services-ads:23.1.0")

    implementation(libs.sqlite)

//    implementation(libs.roomRuntime)
//    ksp(libs.roomCompiler)
//    implementation(libs.roomKtx)

    implementation(libs.jsoup)
    implementation(libs.kuromoji)
}