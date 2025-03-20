import java.io.File
import psi.*
import env.*
import io.IndentWriter
import io.intentWriter
import task.*
import java.text.DateFormat
import java.util.Date

abstract class Kotlin2Dart : AbstractKotlin2TypeTask() {

    private val env = createEnv()
    private val linkEnv = createEnv()

    private val linkMap = mutableMapOf<String, Node>()
    private lateinit var out: File
    private val dateFormat = DateFormat.getDateTimeInstance()

    private val typeMap = mapOf(
        "Boolean" to "bool",
        "Byte" to "int",
        "Short" to "int",
        "Int" to "int",
        "Uint" to "int",
        "Long" to "int",
        "ULong" to "int",
        "Float" to "double",
        "Double" to "double",
        "Any" to "dynamic"
    )

    override fun preRun() {
        linkEnv.addKotlinSourceRoots(links.toList())
        env.addKotlinSourceRoots(sourceDirs.get())
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
            "\npart of 'api.dart';\n"
        )

        mutableListOf<ClassNode>().apply {
            env.parseNodes {
                linkMap[it] = this
                if (this is ClassNode)
                    add(this)
            }
        }.forEach {
            println("gen ${it.name}...")
            writer.writeClass(it)
        }

        writer.close()
    }


    private fun IndentWriter.writeDoc(docs: List<String>) {
        if (docs.isEmpty())
            return
        docs.forEach { docLine ->
            var i = 0
            val ps = docLine.lines()
            ps.forEach {
                i++
                if (i < ps.size || !it.isBlank()) {
                    writeln("/// ", it)
                }
            }
        }
    }

    private fun IndentWriter.writeProperty(prop: PropertyNode) {
        writeDoc(prop.docs)
        val t = prop.type

        if (t.isSub) {
            val rt = linkMap[t.name]
            when (rt) {
                is EnumNode -> {
                    write("/// ")
                    writeln(rt.values.joinToString(" | "))
                    write("String ")
                }

                else -> throw IllegalArgumentException("sub type must be enum: $rt")
            }
        } else {
            write(typeMap[t.mainType] ?: t.mainType)
            if (t.subTypes.isNotEmpty()) {
                write(t.subTypes.joinToString(prefix = "<", postfix = ">") {
                    typeMap[it] ?: it
                })
            }
            if (t.nullable)
                write("?")
            write(" ")
        }

        write(prop.name)
        write(";")
        newLine()
    }

    private fun IndentWriter.writeConstructor(name: String, props: List<PropertyNode>) {
        write(name)
        write("({")
        props.forEach { prop ->
            if (!prop.type.nullable)
                write("required ")
            write("this.")
            write(prop.name)
            write(",")
        }
        write("});")
        newLine()
    }

    private fun IndentWriter.writeFromJson(name: String, props: List<PropertyNode>) {
        write("factory ")
        write(name)
        write(".fromJson(Map<String, dynamic> json) => ")
        write(name)
        write("(")
        newLine()
        props.forEach { prop ->
            indent {
                val list = prop.type.subTypes.isNotEmpty();

                write(prop.name)
                write(": ")
                if (list)
                    write("(")
                write("json['")
                write(prop.name)
                write("'] as ")
                if (prop.type.isSub) {
                    when (linkMap[prop.type.name]) {
                        is EnumNode -> write("String")
                        else -> throw IllegalArgumentException("sub type must be enum: ${prop.type.name}")
                    }
                } else {
                    if (list)
                        write("List<dynamic>")
                    else
                        write(typeMap[prop.type.name] ?: prop.type.name)
                }
                if (prop.type.nullable)
                    write("?")
                if (list) {
                    write(")")
                    newLine()
                    write("    .map((e) => ")
                    val st = prop.type.subTypes[0]
                    if (typeMap[st] != null) {
                        write("(e as ")
                        write(typeMap[st]!!)
                        write("))")
                    } else {
                        write(st)
                        write(".fromJson(e as Map<String, dynamic>))")
                    }
                    newLine()
                    write("    .toList()")
                }
                write(",")
            }
            newLine()
        }
        write(");")
        newLine()
    }

    private fun IndentWriter.writeClass(c: ClassNode) {
        writeDoc(c.docs)
        writeln("class ", c.name, " {")
        c.properties.forEach { prop ->
            indent {
                writeProperty(prop)
            }
        }
        newLine()
        indent {
            writeConstructor(c.name, c.properties)
            newLine()
            writeFromJson(c.name, c.properties)
        }
        writeln("}")
        newLine()
    }

}
