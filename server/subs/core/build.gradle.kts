plugins {
    alias(libs.plugins.license)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    api(annotation())
    api(commomData())
    api(entity())
    api(service())
    api(token())
    api(utils())

    api(libs.spring.boot.api.starter)
    api(libs.spring.cloud.starter.netflix.eureka.client)
    api(libs.spring.boot.starter.actuator)
    api(libs.spring.boot.starter.data.jpa)
    implementation("org.jboss.logging:jboss-logging:3.6.1.Final")

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

licenseReport {
    showVersions = true
}
