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
    api(token())
    api(utils())

    api(libs.spring.boot.api.starter) {
        exclude("org.springframework.boot", "spring-boot-starter-undertow")
    }
    api("org.springframework.boot:spring-boot-starter-jetty:3.3.4")
    api(libs.spring.cloud.starter.netflix.eureka.client)
    api(libs.spring.boot.starter.actuator)
    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.data.redis) {
        exclude("io.lettuce", "lettuce-core")
    }
    api("redis.clients:jedis:5.2.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0")
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
