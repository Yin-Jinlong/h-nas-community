package com.yjl.hnas.fs

import java.nio.file.AccessMode

/**
 * @author YJL
 */
interface PathManager<P : AbstractPath<*, *, P>> {

    fun checkAccess(path: P, vararg modes: AccessMode)

}
