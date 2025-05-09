import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

val pkg = "io.github.yinjinlong.hnas"


fun DependencyHandler.server(path: String) = project(mapOf("path" to ":server:$path"))

fun DependencyHandler.annotation() = server("annotation")

fun DependencyHandler.commomData() = server("common-data")

fun DependencyHandler.entity() = server("entity")
fun DependencyHandler.fs() = server("fs")

fun DependencyHandler.utils() = server("utils")

fun DependencyHandler.serviceCore() = server("subs:core")
