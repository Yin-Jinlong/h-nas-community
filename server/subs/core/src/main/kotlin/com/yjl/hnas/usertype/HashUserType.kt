package com.yjl.hnas.usertype

import com.yjl.hnas.entity.Hash
import org.hibernate.type.descriptor.java.BasicJavaType
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType
import org.hibernate.type.descriptor.jdbc.JdbcType
import org.hibernate.usertype.BaseUserTypeSupport
import java.util.function.BiConsumer

/**
 * @author YJL
 */
class HashUserType : BaseUserTypeSupport<Hash>() {
    override fun resolve(resolutionConsumer: BiConsumer<BasicJavaType<Hash>, JdbcType>) {
        resolutionConsumer.accept(HashJavaType.INSTANCE, BinaryJdbcType.INSTANCE)
    }
}
