package com.yjl.hnas.services

import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.service.UserFileService
import org.springframework.stereotype.Service
import java.nio.file.AccessMode

/**
 * @author YJL
 */
@Service
class UserFileServiceImpl : UserFileService {
    override fun checkAccess(path: UserFilePath, vararg modes: AccessMode) {
        TODO("Not yet implemented")
    }

    override fun createDirectory(path: UserFilePath) {
        TODO("Not yet implemented")
    }
}