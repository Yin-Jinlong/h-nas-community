package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.fs.BadPathException
import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.service.TooManyChildrenException
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.NoSuchFileException
import java.nio.file.NotDirectoryException

/**
 * @author YJL
 */
abstract class WithFS(
    val fsp: VirtualFileSystemProvider,
) {
    val fs = fsp.virtualFilesystem

    fun getPath(private: Boolean, user: Uid?, path: String) = try {
        if (private && user == null)
            throw ErrorCode.BAD_ARGUMENTS.error
        if (private)
            fs.getUserPath(user!!, path)
        else
            fs.getPubPath(path)
    } catch (e: BadPathException) {
        throw ErrorCode.BAD_ARGUMENTS.data(path)
    }.toAbsolutePath()

    fun <R> withCatch(block: () -> R) = try {
        block()
    } catch (e: NoSuchFileException) {
        throw ErrorCode.NO_SUCH_FILE.data(e.file)
    } catch (e: FileAlreadyExistsException) {
        throw ErrorCode.FILE_EXISTS.data(e.file)
    } catch (e: TooManyChildrenException) {
        throw ErrorCode.TOO_MANY_CHILDREN.data(e.file)
    } catch (e: DirectoryNotEmptyException) {
        throw ErrorCode.FOLDER_NOT_EMPTY.data(e.file)
    } catch (e: BadPathException) {
        throw ErrorCode.BAD_ARGUMENTS.data("非法文件名")
    } catch (e: NotDirectoryException) {
        throw ErrorCode.NOT_FOLDER.data(e.file)
    }
}
