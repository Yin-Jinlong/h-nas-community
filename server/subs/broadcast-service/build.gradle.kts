plugins {
    alias(libs.plugins.license)
    alias(libs.plugins.kotlin)
}

dependencies {
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation(libs.gson)
    implementation(commomData())

    testImplementation(libs.kotlin.test.junit5)
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
