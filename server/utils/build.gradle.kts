plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    api(libs.tika.core)
    api(libs.tika)

    api("io.github.yin-jinlong:message-digest-kotlin:0.1.3")
}
