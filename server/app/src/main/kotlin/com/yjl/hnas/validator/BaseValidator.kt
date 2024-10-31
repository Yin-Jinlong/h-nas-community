package com.yjl.hnas.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

typealias ValidateFn<A, T> = (anno: A, value: T?, ctx: ConstraintValidatorContext) -> Boolean

/**
 * @author YJL
 */
abstract class BaseValidator<A : Annotation, T>(
    private val validateFn: ValidateFn<A, T>
) : ConstraintValidator<A, T> {

    lateinit var anno: A

    override fun initialize(constraintAnnotation: A) {
        anno = constraintAnnotation
    }

    override fun isValid(value: T, context: ConstraintValidatorContext): Boolean {
        return validateFn(anno, value, context)
    }
}