import org.springframework.boot.gradle.tasks.bundling.BootJar
import utils.buildImageTask
import utils.configBootJar

plugins {
    alias(libs.plugins.license)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(serviceCore())
    implementation(libs.spring.ai.starter.model.chat.memory)
    implementation(platform(libs.spring.ai.bom))
    // implementation(libs.spring.ai.spring.boot.autoconfigure)
    implementation(libs.spring.ai.ollama.spring.boot.starter)
    implementation(libs.lunar)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mybatis.spring.boot.starter.test)
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

tasks.withType<BootJar> {
    configBootJar(this)
}

buildImageTask()

licenseReport {
    showVersions = true
}
