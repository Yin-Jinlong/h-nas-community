enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://maven.aliyun.com/repository/gradle-plugin/")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        mavenCentral()
    }
}

rootProject.name = "h-nas"

private fun Settings.isProject(path: String): Boolean {
    val buildFile = File(rootDir, "$path/build.gradle.kts")
    return buildFile.exists()
}

private fun Settings.includeSub(paths: Array<String>) {
    include(paths.joinToString(":"))
}

typealias Filter = (path: String) -> Boolean

private fun Settings.includeSubs(paths: Array<String>, filter: Filter) {
    val dir = if (paths.isEmpty()) rootDir
    else File(rootDir, paths.joinToString("/"))
    dir.listFiles()?.forEach { sub ->
        if (sub.isDirectory) {
            val ps = arrayOf(*paths, sub.name)
            val path = ps.joinToString("/")
            if (filter(path)) {
                if (isProject(path)) {
                    includeSub(ps)
                }
                includeSubs(ps, filter)
            }
        }
    }
}

val ignoreRoots = Regex("(\\.|compose|web/).*|buildSrc|gradle|data|cache")
val ignoreContains = setOf(
    "src",
    "build"
)

includeSubs(arrayOf()) {
    !it.matches(ignoreRoots) && !ignoreContains.any { ignore -> it.contains(ignore) }
}
