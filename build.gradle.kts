plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.license) apply false
}

group = "io.github.yinjinlong"
version = "1.0.0"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
