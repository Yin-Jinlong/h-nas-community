package com.yjl.hnas.fs

/**
 * @author YJL
 */
interface PubPathManager : PathManager<PubPath> {

    fun toVirtualPath(path: PubPath): VirtualPath

}
