plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly("io.github.yin-jinlong:message-digest-kotlin:0.1.4")
    compileOnly("com.google.code.gson:gson:2.11.0")

    implementation(utils())
}
