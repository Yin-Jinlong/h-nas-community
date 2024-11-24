package com.yjl.hnas.controller

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.BadPathException
import com.yjl.hnas.fs.VirtualFileSystemProvider
import java.nio.file.FileAlreadyExistsException
import java.nio.file.NoSuchFileException

/**
 * @author YJL
 */
abstract class WithFS(
    val fsp: VirtualFileSystemProvider,
) {
    val fs = fsp.virtualFilesystem

    fun getPubPath(path: String) = try {
        fs.getPubPath(path)
    } catch (e: BadPathException) {
        throw ErrorCode.BAD_ARGUMENTS.data(path)
    }

    fun getUserPath(uid: Uid, path: String) = try {
        fs.getUserPath(uid, path).toAbsolutePath()
    } catch (e: BadPathException) {
        throw ErrorCode.BAD_ARGUMENTS.data(path)
    }

    fun <R> withCatch(block: () -> R) = try {
        block()
    } catch (e: NoSuchFileException) {
        throw ErrorCode.NO_SUCH_FILE.data(e.file)
    } catch (e: FileAlreadyExistsException) {
        throw ErrorCode.FILE_EXISTS.data(e.file)
    }
}
