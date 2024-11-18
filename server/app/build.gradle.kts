plugins {
    java
    alias(libs.plugins.kotlin)
    alias(libs.plugins.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(annotation())
    implementation(commomData())
    implementation(entity())
    implementation(fs())
    implementation(service())
    implementation(token())
    implementation(utils())

    implementation(libs.spring.boot.api.starter)
    implementation(libs.webp.imageio)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

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
