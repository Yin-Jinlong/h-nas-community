package utils

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Exec
import org.gradle.jvm.tasks.Jar

private val NamedDomainObjectContainer<Configuration>.runtimeClasspath: NamedDomainObjectProvider<Configuration>
    get() = named("runtimeClasspath", Configuration::class.java)

/**
 * @author YJL
 */
fun Project.configBootJar(
    jar: Jar,
    excludeArchs: Set<String> = setOf("arm", "arm64", "arm64-gpl", "armhf", "ppc64le", "x86")
) = with(jar) {
    exclude(excludeArchs.map { "*-$it.jar" })
    Unit
}

/**
 * @author YJL
 */
fun Project.buildImageTask() = tasks.register("image", Exec::class) {
    group = "build"
    dependsOn("bootJar")
    commandLine(
        "docker",
        "build",
        "-t",
        "${rootProject.name}/${project.name}:${rootProject.version}",
        project.projectDir.path
    )
}
