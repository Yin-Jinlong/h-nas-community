package com.yjl.hnas.utils

import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedMethod
import kotlin.reflect.KClass

private val indirectMap = mutableMapOf<Class<*>, Boolean>()

/**
 * 是否包含或间接包含注解
 *
 * 被包含的注解，如果可以间接则必须直接使用[Target]并包含[AnnotationTarget.ANNOTATION_CLASS]
 */
private fun <A : Annotation> Annotation.hasOrIs(anno: KClass<A>): Boolean {
    if (anno.java !in indirectMap) {
        // 获取注解目标
        val targets: Array<out Target>? = (anno as Any).javaClass.getAnnotationsByType(Target::class.java)
        // 没有则不可以间接
        if (targets == null || targets.isEmpty()) {
            indirectMap[anno.java] = false
        } else {
            // 遍历所有，如果任意有
            indirectMap[anno.java] = targets.any {
                it.allowedTargets.contains(AnnotationTarget.ANNOTATION_CLASS)
            }
        }
    }
    return this::class == anno ||
            // 传递
            if (indirectMap[anno.java]!!) javaClass.annotations.any { it.hasOrIs(anno) }
            // 直接
            else javaClass.isAnnotationPresent(anno.java)
}

/**
 * 是否包含或间接包含注解
 */
fun <A : Annotation> Class<*>.hasAnno(anno: KClass<A>): Boolean {
    return annotations.any { it.hasOrIs(anno) }
}

/**
 * 是否包含或间接包含注解
 */
fun <A : Annotation> KClass<*>.hasAnno(anno: KClass<A>) = java.hasAnno(anno)

/**
 * 是否包含或间接包含注解
 *
 * - 参数注解 > 方法注解 > 类注解
 */
fun <A : Annotation> MethodParameter.hasAnno(anno: KClass<A>): Boolean {
    return parameter::class.hasAnno(anno) ||
            method::class.hasAnno(anno) ||
            declaringClass.hasAnno(anno)
}

/**
 * 是否包含或间接包含注解
 *
 * - 方法注解 > 类注解
 */
fun <A : Annotation> AnnotatedMethod.hasAnno(anno: KClass<A>): Boolean {
    return method::class.hasAnno(anno) ||
            method.declaringClass.hasAnno(anno)
}
