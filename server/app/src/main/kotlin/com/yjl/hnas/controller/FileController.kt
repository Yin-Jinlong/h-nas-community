package com.yjl.hnas.controller

import io.github.yinjinlong.spring.boot.util.getLogger
import org.springframework.web.bind.annotation.RestController

/**
 * @author YJL
 */
@RestController
class FileController {
    private val logger = getLogger()
//
//    @Autowired
//    lateinit var fileMappingService: FileMappingService
//
//    @Autowired
//    lateinit var folderService: FolderService
//
//    @Async
//    @GetMapping("api/file/get/**")
//    fun get(req: HttpServletRequest): File {
//        val path = req.servletPath
//        val file = path.substring(14)
//        return FileUtils.DATA_DIR
//            .resolve(URLDecoder.decode(file, Charsets.UTF_8))
//    }

}
