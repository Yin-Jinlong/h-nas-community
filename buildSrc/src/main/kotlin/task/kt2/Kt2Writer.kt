package task.kt2

import io.IndentWriter
import psi.ClassNode

/**
 * @author YJL
 */
interface Kt2Writer {

    fun write(out: IndentWriter, c: ClassNode)

}