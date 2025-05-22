package io.github.yinjinlong.hnas.entity

/**
 *
 * @author YJL
 */
interface IFileMapping : FileWithType {

    var hash: Hash

    var dataPath: String

    var preview: Boolean

    var size: Long

    companion object{
        const val TABLE = "file_mapping"
    }
}
