package task.kt2

import io.intentWriter
import psi.ClassNode
import psi.EnumNode
import java.text.DateFormat
import java.util.*

/**
 * @author YJL
 */
abstract class Kotlin2Dart : AbstractKotlin2TypeTask() {
    private val dateFormat = DateFormat.getDateTimeInstance()

    override fun run(classes: Set<ClassNode>) {
        val out = outputFile.get().asFile
        if (!out.exists())
            out.delete()

        val writer = out.intentWriter(2)
        writer.writeln(
            "// generated at ",
            dateFormat.format(Date()),
        )

        writer.writeln("typedef JsonObject = Map<String, dynamic>;")
        writer.newLine()

        val classWriter = Kt2DartClassWriter(::getLinkClass)
        val enumWriter = Kt2DartEnumWriter(::getLinkClass)

        classes.forEach {
            println("gen ${it.name}...")
            when (it) {
                is EnumNode -> enumWriter.write(writer, it)
                else -> classWriter.write(writer, it)
            }
            writer.newLine()
        }

        writer.close()
    }

}