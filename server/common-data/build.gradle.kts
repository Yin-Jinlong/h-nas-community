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

afterEvaluate {
    tasks.create("genDts", Kotlin2DTS::class) {
        group = "build"
        outputFile = File(
            project(":web").layout.projectDirectory.asFile.path,
            "global-types.d.ts"
        )
        links.addAll(
            listOf(
                project(":server:entity").layout.projectDirectory.asFile,
            )
        )
        sourceDirs.add(
            project.layout.projectDirectory
        )
    }

    // 每次编译时都生成dts
    tasks.getByName("classes") {
        dependsOn("genDts")
    }

}

