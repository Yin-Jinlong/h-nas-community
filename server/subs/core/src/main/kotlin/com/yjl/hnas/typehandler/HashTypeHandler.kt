package com.yjl.hnas.typehandler

import com.yjl.hnas.entity.Hash
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author YJL
 */
class HashTypeHandler : BaseTypeHandler<Hash>() {

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: Hash, jdbcType: JdbcType?) {
        ps.setBinaryStream(i, parameter.inputStream(), parameter.size)
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): Hash? {
        return rs.getBytes(columnName)?.let { Hash(it) }
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): Hash? {
        return rs.getBytes(columnIndex)?.let { Hash(it) }
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): Hash? {
        return cs.getBytes(columnIndex)?.let { Hash(it) }
    }
}
