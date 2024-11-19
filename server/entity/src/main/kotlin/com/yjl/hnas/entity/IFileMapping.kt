package com.yjl.hnas.entity

/**
 *
 * @author YJL
 */
interface IFileMapping : FileWithType {

    var hash: String

    var dataPath: String

    var size: Long

}
