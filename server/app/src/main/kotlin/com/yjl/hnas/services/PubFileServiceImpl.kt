package com.yjl.hnas.services

import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.service.PubFileService
import org.springframework.stereotype.Service
import java.nio.file.AccessMode

/**
 * @author YJL
 */
@Service
class PubFileServiceImpl : PubFileService {
    override fun checkAccess(path: PubPath, vararg modes: AccessMode) {

    }

    override fun toVirtualPath(path: PubPath): VirtualPath {
        TODO("Not yet implemented")
    }
}