plugins {
    alias(libs.plugins.license)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {

    implementation(fs())
    implementation(serviceCore())
    implementation(libs.webp.imageio)
    implementation(libs.bytedeco.javacv)
    implementation(libs.bytedeco.javacv.platform)
    implementation(libs.bytedeco.opencv.platform.gpu)
    implementation(libs.bytedeco.ffmpeg.platform.gpl)
    implementation(libs.jaudiotagger)
    implementation(libs.commons.compress)
    implementation(libs.pdfbox)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mybatis.spring.boot.starter.test)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

licenseReport {
    showVersions = true
}
