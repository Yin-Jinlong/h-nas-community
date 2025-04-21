package task.kt2

import io.IndentWriter
import psi.ClassNode
import psi.EnumNode
import utils.dartString
import utils.underline2smallCamel

/**
 * @author YJL
 */
class Kt2DartEnumWriter(
    getLinkClass: GetLinkClassFn
) : Kt2DartClassWriter(getLinkClass) {

    companion object {
        fun check(c: ClassNode): EnumNode {
            if (c !is EnumNode)
                throw IllegalArgumentException("${c.name} must be EnumNode")
            return c
        }
    }

    override val type: String = "enum"

    override fun writeProperties(out: IndentWriter, c: ClassNode) = with(out) {
        val e = check(c)
        e.values.forEachIndexed { index, v ->
            writeln(v.underline2smallCamel, (if (index != e.values.lastIndex) "," else ";"))
        }
    }

    override fun writeUtils(out: IndentWriter, c: ClassNode) = with(out) {
        val e = check(c)
        // of 函数
        write("static ")
        write(c.dartName)
        writeln("? of(String? str) => switch (str?.toLowerCase()) {")
        indent {
            e.values.forEach {
                val name = it.lowercase()
                write(name.dartString)
                write(" => ")
                write(c.dartName)
                write(".")
                write(name.underline2smallCamel)
                writeln(",")
            }
            writeln("_ => null,")
        }
        writeln("};")
        newLine()

        // ofOrDefault
        write("static ")
        write(c.dartName)
        write(" ofOrDefault(String? str,")
        write(c.dartName)
        writeln(" def) => of(str)??def;")

    }
}