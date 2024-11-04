package utils

val String.kebab: String
    get() = replace("([a-z])([A-Z]+)".toRegex(), "$1-$2").lowercase()
