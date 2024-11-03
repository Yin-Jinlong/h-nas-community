plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly("com.google.code.gson:gson:2.11.0")

    implementation(utils())
}
