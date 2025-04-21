package task.kt2

import io.IndentWriter

/**
 * @author YJL
 */
abstract class AbstractKt2Writer : Kt2Writer {

    /**
     * 输出带大括号的代码块
     *
     * @param pres 输出前缀
     * @param block 括号内内容
     */
    protected fun IndentWriter.writeBreakBlock(vararg pres: String, block: () -> Unit) {
        writeln(*pres, "{")
        block()
        writeln("}")
    }

    protected fun IndentWriter.writeDoc(docs: List<String>) {
        if (docs.isEmpty())
            return
        docs.forEach { docLine ->
            var i = 0
            val ps = docLine.lines()
            ps.forEach {
                i++
                if (i < ps.size || it.isNotBlank()) {
                    writeln("/// ", it)
                }
            }
        }
    }
}
