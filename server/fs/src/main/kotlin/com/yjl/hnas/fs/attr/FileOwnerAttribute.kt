package com.yjl.hnas.fs.attr

/**
 * @author YJL
 */
class FileOwnerAttribute(
    owner: Long
) : FileAttribute<Long>(NAME, owner) {
    companion object {
        const val NAME = "owner"
    }
}
