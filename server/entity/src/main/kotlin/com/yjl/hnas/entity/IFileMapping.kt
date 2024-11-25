package com.yjl.hnas.entity

/**
 *
 * @author YJL
 */
interface IFileMapping : FileWithType {

    var hash: Hash

    var dataPath: String

    var preview: Boolean

    var size: Long

}
