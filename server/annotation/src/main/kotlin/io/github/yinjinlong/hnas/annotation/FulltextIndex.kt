package io.github.yinjinlong.hnas.annotation

/**
 * @author YJL
 */
@Target(AnnotationTarget.PROPERTY)
annotation class FulltextIndex(
    val name: String = "",
)
