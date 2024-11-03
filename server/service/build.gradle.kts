plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(entity())
    compileOnly(commomData())
    compileOnly(token())
    
    testImplementation(kotlin("test"))
}


tasks.withType<Test> {
    useJUnitPlatform()
}
