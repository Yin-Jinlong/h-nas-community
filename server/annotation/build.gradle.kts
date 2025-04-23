plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(libs.spring.core)

    api(libs.hibernate.commons.annotations)
}
