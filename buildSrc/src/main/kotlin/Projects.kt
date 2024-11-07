import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

val pkg = "com.yjl.hnas"


fun DependencyHandler.server(path: String) = project(mapOf("path" to ":server:$path"))

fun DependencyHandler.annotation() = server("annotation")

fun DependencyHandler.commomData() = server("common-data")

fun DependencyHandler.entity() = server("entity")
fun DependencyHandler.fs() = server("fs")

fun DependencyHandler.service() = server("service")

fun DependencyHandler.token() = server("token")

fun DependencyHandler.utils() = server("utils")
