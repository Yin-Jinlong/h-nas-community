package com.yjl.hnas.converter

import com.yjl.hnas.entity.Hash
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * @author YJL
 */
@Converter(autoApply = true)
class HashConverter : AttributeConverter<Hash, ByteArray> {
    override fun convertToDatabaseColumn(attribute: Hash?): ByteArray? {
        return attribute?.bytes
    }

    override fun convertToEntityAttribute(dbData: ByteArray?): Hash? {
        return dbData?.let { Hash(it) }
    }
}