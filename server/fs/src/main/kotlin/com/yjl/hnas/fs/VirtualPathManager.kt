package com.yjl.hnas.fs

import java.io.File

/**
 * @author YJL
 */
interface VirtualPathManager : PathManager<VirtualPath> {

    fun convertToFile(path: VirtualPath): File

}
