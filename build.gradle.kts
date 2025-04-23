plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.license) apply false
}

group = "io.github.yinjinlong"
version = "0.0.1-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
