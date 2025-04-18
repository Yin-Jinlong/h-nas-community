plugins {
    alias(libs.plugins.license)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
//    maven("https://maven.aliyun.com/repository/public")
//    maven("https://maven.aliyun.com/repository/central")
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
    implementation("com.github.Adonai:jaudiotagger:2.3.14")
    implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("org.apache.pdfbox:pdfbox:3.0.3")

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
