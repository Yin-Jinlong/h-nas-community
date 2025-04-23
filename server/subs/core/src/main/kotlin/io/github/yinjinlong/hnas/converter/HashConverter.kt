package io.github.yinjinlong.hnas.converter

import io.github.yinjinlong.hnas.entity.Hash
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