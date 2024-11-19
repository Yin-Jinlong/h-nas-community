plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    compileOnly(annotation())
    compileOnly(utils())
    compileOnly(libs.spring.boot.api.starter)

}
repositories {
    mavenCentral()
}
