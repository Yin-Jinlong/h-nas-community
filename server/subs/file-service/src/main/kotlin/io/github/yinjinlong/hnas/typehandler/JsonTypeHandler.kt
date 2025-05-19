package io.github.yinjinlong.hnas.typehandler

import com.google.gson.Gson
import com.google.gson.JsonElement
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author YJL
 */
@MappedTypes(JsonElement::class)
class JsonTypeHandler : BaseTypeHandler<JsonElement>() {

    val gson = Gson()

    fun toJsonTree(str: String): JsonElement? {
        return gson.fromJson(str, JsonElement::class.java)
    }

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: JsonElement, jdbcType: JdbcType?) {
        ps.setString(i, gson.toJson(parameter))
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): JsonElement? {
        return rs.getString(columnName)?.let { toJsonTree(it) }
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): JsonElement? {
        return rs.getString(columnIndex)?.let { toJsonTree(it) }
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): JsonElement? {
        return cs.getString(columnIndex)?.let { toJsonTree(it) }
    }
}
