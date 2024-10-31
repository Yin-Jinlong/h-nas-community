plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    compileOnly(annotation())
    compileOnly(utils())
    compileOnly(libs.spring.boot.api.starter)

    implementation("org.jboss.logging:jboss-logging:3.6.1.Final")
    api(libs.spring.boot.starter.data.jpa)


}
repositories {
    mavenCentral()
}
