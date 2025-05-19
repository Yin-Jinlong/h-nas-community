package io.github.yinjinlong.hnas.usertype

import com.google.gson.Gson
import com.google.gson.JsonElement
import org.hibernate.type.SqlTypes
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.type.descriptor.java.AbstractClassJavaType
import org.hibernate.type.descriptor.jdbc.AdjustableJdbcType
import org.hibernate.type.descriptor.jdbc.JdbcType
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators

/**
 * @author YJL
 */
class JsonJavaType : AbstractClassJavaType<JsonElement>(JsonElement::class.java) {

    companion object {
        val INSTANCE = JsonJavaType()
    }

    val gson = Gson()

    override fun getRecommendedJdbcType(indicators: JdbcTypeIndicators): JdbcType {
        val descriptor = indicators.getJdbcType(indicators.resolveJdbcTypeCode(SqlTypes.JSON))
        return if (descriptor is AdjustableJdbcType)
            descriptor.resolveIndicatedType(indicators, this)
        else
            descriptor
    }

    override fun fromString(string: CharSequence?): JsonElement? {
        return string?.let { gson.toJsonTree(string) }
    }

    override fun <X : Any?> unwrap(
        value: JsonElement?,
        type: Class<X?>,
        options: WrapperOptions
    ): X? {
        if (value == null) return null
        return when (type) {

            JsonElement::class.java -> value

            String::class.java -> gson.toJson(value)

            else -> gson.fromJson(value, type)
        } as? X
    }

    override fun <X : Any?> wrap(
        value: X?,
        options: WrapperOptions
    ): JsonElement? {
        if (value == null) return null
        return when (value) {
            is String -> gson.fromJson(value, JsonElement::class.java)
            is JsonElement -> value
            else -> throw unknownWrap(value::class.java)
        }
    }

}
