plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(entity())
    testImplementation(kotlin("test"))

}


tasks.withType<Test> {
    useJUnitPlatform()
}
