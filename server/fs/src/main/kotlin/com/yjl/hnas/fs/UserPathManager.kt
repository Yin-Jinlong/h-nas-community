package com.yjl.hnas.fs

/**
 * @author YJL
 */
interface UserPathManager : PathManager<UserFilePath> {

    fun createDirectory(path: UserFilePath)

}