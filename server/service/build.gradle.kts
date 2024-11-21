plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(entity())
    compileOnly(commomData())
    compileOnly(fs())
    compileOnly(token())
    compileOnly(utils())

    testImplementation(kotlin("test"))
}


tasks.withType<Test> {
    useJUnitPlatform()
}
