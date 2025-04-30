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
    api(utils())

    api(libs.spring.boot.api.starter)
    api(libs.spring.boot.starter.actuator)
    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.data.redis) {
        exclude("io.lettuce", "lettuce-core")
    }
    api(libs.jedis)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.core.jvm)
    api(libs.spring.cloud.starter.bootstrap)
    api(libs.spring.cloud.starter.loadbalancer)
    api(libs.spring.cloud.starter.alibaba.nacos.config)
    api(libs.spring.cloud.starter.alibaba.nacos.discovery)
    implementation(libs.jboss.logging)

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
