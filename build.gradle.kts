plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.license) apply false
}

group = "com.yjl"
version = "0.0.1-SNAPSHOT"

allprojects {
    repositories{
        google()
        // maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        mavenCentral()
    }
}
