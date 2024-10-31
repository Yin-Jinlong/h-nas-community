package com.yjl.hnas.mybatis.typehandler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedJdbcTypes
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author YJL
 */
@MappedTypes(List::class)
@MappedJdbcTypes(JdbcType.VARCHAR)
class StringListTypeHandler : BaseTypeHandler<List<String>>() {
    override fun setNonNullParameter(
        ps: PreparedStatement,
        i: Int,
        parameter: List<String>,
        jdbcType: JdbcType?
    ) {
        ps.setString(i, parameter.joinToString(","))
    }

    override fun getNullableResult(rs: ResultSet?, columnName: String): List<String> {
        return rs?.getString(columnName)?.split(",") ?: listOf()
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): List<String> {
        return rs?.getString(columnIndex)?.split(",") ?: listOf()
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): List<String> {
        return cs?.getString(columnIndex)?.split(",") ?: listOf()
    }
}