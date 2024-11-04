import java.io.File
import psi.*
import env.*
import io.IndentWriter
import io.intentWriter
import task.*
import java.text.DateFormat
import java.util.Date

abstract class Kotlin2DTS : AbstractKotlin2DTSTask() {

    private val env = createEnv()
    private val linkEnv = createEnv()

    private val linkMap = mutableMapOf<String, Node>()
    private lateinit var out: File
    private val dateFormat = DateFormat.getDateTimeInstance()

    private val typeMap = mapOf(
        "Boolean" to "boolean",
        "Byte" to "number",
        "Short" to "number",
        "Int" to "number",
        "Uint" to "number",
        "Long" to "number",
        "ULong" to "number",
        "Float" to "number",
        "Double" to "number",
        "String" to "string",
        "Any" to "any"
    )

    override fun preRun() {
        linkEnv.addKotlinSourceRoots(links.toList())
        env.addKotlinSourceRoots(sourceDirs.get().map { it.asFile })
        linkEnv.parseNodes {
            linkMap[it] = this
        }
        out = outputFile.asFile.get()
    }

    override fun run() {
        if (!out.exists())
            out.delete()

        val writer = out.intentWriter()
        writer.writeln(
            "// generated at ",
            dateFormat.format(Date()),
            "\nglobal {"
        )

        mutableListOf<ClassNode>().apply {
            env.parseNodes {
                linkMap[it] = this
                if (this is ClassNode)
                    add(this)
            }
        }.forEach {
            println("gen ${it.name}...")
            writer.indent {
                writeClass(it)
            }
        }

        writer.writeln("}")
        writer.writeln("export {}")
        writer.close()
    }


    private fun IndentWriter.writeDoc(docs: List<String>) {
        if (docs.isEmpty())
            return
        writeln("/**")
        docs.forEach { docLine ->
            var i = 0
            val ps = docLine.lines()
            ps.forEach {
                i++
                if (i < ps.size || !it.isBlank()) {
                    writeln(" * ", it)
                }
            }
        }
        writeln(" */")
    }

    private fun IndentWriter.writeProperty(prop: PropertyNode) {
        writeDoc(prop.docs)
        write(prop.name)
        val t = prop.type
        if (t.nullable)
            write("?")
        write(": ")
        if (t.isSub) {
            val rt = linkMap[t.name]
            when (rt) {
                is EnumNode -> write(rt.values.joinToString(" | ") {
                    "'$it'"
                })

                else -> throw IllegalArgumentException("sub type must be enum: $rt")
            }
        } else
            write(typeMap[t.name] ?: t.name)
        newLine()
    }

    private fun IndentWriter.writeClass(c: ClassNode) {
        writeDoc(c.docs)
        writeln("export declare interface ", c.name, " {")
        c.properties.forEach { prop ->
            indent {
                writeProperty(prop)
            }
        }
        writeln("}")
        newLine()
    }

}
