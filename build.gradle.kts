plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.license) apply false
}

group = "com.yjl"
version = "0.0.1-SNAPSHOT"
