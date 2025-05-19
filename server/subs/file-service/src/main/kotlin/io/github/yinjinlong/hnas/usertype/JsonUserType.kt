package io.github.yinjinlong.hnas.usertype

import com.google.gson.JsonElement
import org.hibernate.type.descriptor.java.BasicJavaType
import org.hibernate.type.descriptor.jdbc.JdbcType
import org.hibernate.type.descriptor.jdbc.JsonJdbcType
import org.hibernate.usertype.BaseUserTypeSupport
import java.util.function.BiConsumer

/**
 * @author YJL
 */
class JsonUserType : BaseUserTypeSupport<JsonElement>() {
    override fun resolve(resolutionConsumer: BiConsumer<BasicJavaType<JsonElement>, JdbcType>) {
        resolutionConsumer.accept(JsonJavaType.INSTANCE, JsonJdbcType.INSTANCE)
    }

}
