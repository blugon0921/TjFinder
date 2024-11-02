// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.googleServices) apply false
}
allprojects {
    repositories {
        google()
        maven("https://dl.google.com/dl/android/maven2")
        mavenCentral()
        maven("https://jitpack.io")
    }
}