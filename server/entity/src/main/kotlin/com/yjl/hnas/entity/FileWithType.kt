package com.yjl.hnas.entity

/**
 * @author YJL
 */
interface FileWithType {
    var type: String
    var subType: String


    fun canPreview() = PreviewTypes.any { (t, s) ->
        t == type && (s.isEmpty() || s == subType)
    }

    companion object {

        val PreviewTypes = listOf(
            "image" to "",
            "video" to "",
        )
    }

}