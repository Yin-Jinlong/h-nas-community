plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(commomData())
    compileOnly(entity())

    testImplementation(entity())
    testImplementation(kotlin("test"))
}


tasks.withType<Test> {
    useJUnitPlatform()
}
