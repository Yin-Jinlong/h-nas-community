package io

import java.io.BufferedWriter
import java.io.File
import java.io.Writer

/**
 * @author YJL
 */
class IndentWriter(
    writer: Writer
) : BufferedWriter(writer) {

    private var indent: Int = 0

    private var isNewLine = true

    private fun su(s: String) {
        super.write(s, 0, s.length)
    }

    fun indent(block: IndentWriter.() -> Unit) {
        indent++
        block()
        indent--
    }

    fun writeln(vararg ss: String) {
        ss.forEach(::write)
        newLine()
    }

    override fun write(s: String, off: Int, len: Int) {
        if (isNewLine) {
            for (i in 0 until indent)
                su("    ")
        }
        super.write(s, off, len)
        isNewLine = s.last() == '\n'
    }

    fun <T> write(iterable: Iterable<T>, map: T.() -> String) {
        iterable.forEach {
            val s = it.map()
            super.write(s, 0, s.length)
        }
    }

    override fun newLine() {
        su("\n")
        isNewLine = true
    }

}

fun File.intentWriter(): IndentWriter = IndentWriter(writer())
