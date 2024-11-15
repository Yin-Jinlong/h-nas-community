plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(entity())
    compileOnly(commomData())

    testImplementation(entity())
    testImplementation(kotlin("test"))
}


tasks.withType<Test> {
    useJUnitPlatform()
}
