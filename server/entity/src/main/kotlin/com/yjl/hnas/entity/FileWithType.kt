package com.yjl.hnas.entity

/**
 * @author YJL
 */
interface FileWithType {
    var type: String
    var subType: String

    fun canPreview() = FileMapping.PreviewTypes.any { (t, s) ->
        t == type && (s.isEmpty() || s == subType)
    }

}