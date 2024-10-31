plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(libs.spring.core)

    api("org.hibernate.common:hibernate-commons-annotations:7.0.1.Final")
}
