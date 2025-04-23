plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/central")
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.compiler.embeddable)
}
