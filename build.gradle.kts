// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android) apply false
    alias(libs.plugins.googleServices) apply false
//    id("com.google.devtools.ksp") version "${libs.versions.kotlin.get()}-2.0.0" apply false
}
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://dl.google.com/dl/android/maven2")
        maven("https://jitpack.io")
    }
}