package com.yjl.hnas.validator

import jakarta.validation.Constraint
import kotlin.reflect.KClass

/**
 * @author YJL
 */
@Constraint(validatedBy = [Password.Validator::class])
annotation class Password(
    val message: String = "密码格式错误",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = [],
    val min: Int = 8,
    val max: Int = 18
) {
    companion object {
        val validate: ValidateFn<Password, String> = { anno, value, ctx ->
            if (value == null)
                true
            else if (value.isEmpty())
                false
            else
                value.length in anno.min..anno.max
        }
    }

    class Validator : BaseValidator<Password, String>(validate)
}
