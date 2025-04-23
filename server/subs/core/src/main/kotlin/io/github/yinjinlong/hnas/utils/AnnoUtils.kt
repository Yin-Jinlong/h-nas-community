package io.github.yinjinlong.hnas.utils

import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedMethod
import kotlin.reflect.KClass

private typealias CacheMap<K> = MutableMap<Pair<K, Class<*>>, Boolean>
private typealias CacheClassMap = CacheMap<Class<*>>

private val indirectMap = mutableMapOf<Class<*>, Boolean>()

private val annoResultCache: CacheMap<Annotation> = mutableMapOf()
private val classResultCache: CacheClassMap = mutableMapOf()
private val methodParameterCache: CacheMap<MethodParameter> = mutableMapOf()
private val annotatedMethodCache: CacheMap<AnnotatedMethod> = mutableMapOf()

/**
 * 是否包含或间接包含注解
 *
 * 被包含的注解，如果可以间接则必须直接使用[Target]并包含[AnnotationTarget.ANNOTATION_CLASS]
 */
private fun <A : Annotation> Annotation.hasOrIs(
    anno: Class<A>
): Boolean = annoResultCache.getOrPut(this to anno) {
    this::class == anno || if (indirectMap.getOrPut(anno) { // 缓存
            // 获取注解目标
            val targets: Array<out Target>? =
                (anno as Any).javaClass.getAnnotationsByType(Target::class.java)
            // 没有则不可以间接
            if (targets == null || targets.isEmpty()) {
                false
            } else {
                // 遍历所有，如果任意有
                targets.any {
                    it.allowedTargets.contains(AnnotationTarget.ANNOTATION_CLASS)
                }
            }
        }
    ) javaClass.annotations.any { it.hasOrIs(anno) } // true
    // 直接
    else javaClass.isAnnotationPresent(anno) // false
}


/**
 * 是否包含或间接包含注解
 */
fun <A : Annotation> Class<*>.hasAnno(
    anno: KClass<A>
) = classResultCache.getOrPut(this to anno.java) {
    annotations.any { it.hasOrIs(anno.java) }
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
fun <A : Annotation> MethodParameter.hasAnno(
    anno: KClass<A>
) = methodParameterCache.getOrPut(this to anno.java) {
    parameter.isAnnotationPresent(anno.java) ||
            this.method?.javaClass?.hasAnno(anno) == true ||
            declaringClass.hasAnno(anno)
}

/**
 * 是否包含或间接包含注解
 *
 * - 方法注解 > 类注解
 */
fun <A : Annotation> AnnotatedMethod.hasAnno(
    anno: KClass<A>
) = annotatedMethodCache.getOrPut(this to anno.java) {
    hasMethodAnnotation(anno.java) ||
            method.declaringClass.hasAnno(anno)
}
