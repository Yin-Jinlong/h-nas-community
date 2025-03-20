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
    tasks.create("genTypes", Kotlin2Dart::class) {
        group = "build"
        outputFile = rootProject.layout.projectDirectory.asFile.resolve("client/lib/utils/type.g.dart")

        links.addAll(
            listOf(
                project(":server:entity").layout.projectDirectory.asFile,
            )
        )
        sourceDirs.add(project.layout.projectDirectory.asFile.resolve("src/main/kotlin"))
    }

    // 每次编译时都生成dts
    tasks.getByName("classes") {
        dependsOn("genTypes")
    }

}

