package task.kt2

import io.IndentWriter
import psi.ClassNode
import psi.EnumNode
import psi.PropertyNode
import utils.dartClassName
import utils.dartString

typealias GetLinkClassFn = (String) -> ClassNode?

/**
 * @author YJL
 */
open class Kt2DartClassWriter(
    val getLinkClass: GetLinkClassFn
) : AbstractKt2Writer() {

    companion object {
        internal val typeMap = mapOf(
            "Boolean" to "bool",
            "Byte" to "int",
            "Short" to "int",
            "Int" to "int",
            "Uint" to "int",
            "Long" to "int",
            "ULong" to "int",
            "Float" to "double",
            "Double" to "double",
            "String" to "String",
            "Any" to "dynamic"
        )
    }

    protected open val type: String = "class"

    private fun IndentWriter.writeProperty(prop: PropertyNode) {
        writeDoc(prop.docs)
        val t = prop.type

        write("final ")
        write(typeMap[t.mainType] ?: t.mainType.dartClassName)
        if (t.subTypes.isNotEmpty()) {
            write(t.subTypes.joinToString(prefix = "<", postfix = ">") {
                typeMap[it] ?: it
            })
        }
        if (t.nullable)
            write("?")
        write(" ")
        write(prop.name)
        write(";")
        newLine()
    }

    protected open fun writeProperties(out: IndentWriter, c: ClassNode) {
        for (prop in c.properties)
            out.writeProperty(prop)
    }

    protected open fun writeConstructor(out: IndentWriter, c: ClassNode) = with(out) {
        write("const ")
        write(c.fullName.dartClassName)
        write("(")
        if (c.properties.isNotEmpty()) {
            writeln("{")
            indent {
                c.properties.forEach { prop ->
                    if (!prop.type.nullable)
                        write("required ")
                    write("this.")
                    write(prop.name)
                    writeln(",")
                }
            }
            write("}")
        }
        write(");")
        newLine()
    }

    private fun json(name: String, json: String = "json") = "$json[$name]"

    private fun dartType(type: String, onDartType: (String) -> Unit, onCustomType: (String, ClassNode?) -> Unit) {
        val dt = typeMap[type]
        if (dt != null)
            onDartType(dt)
        else
            onCustomType(type, getLinkClass(type))
    }

    private fun IndentWriter.writeFromJsonItem(prop: PropertyNode) {
        val dartPropName = prop.name.dartString

        write(prop.name)
        write(": ")

        if (prop.type.subTypes.isEmpty()) {
            dartType(prop.type.mainType, onDartType = {
                write(json(dartPropName))
            }) { t, c ->
                if (prop.type.nullable) {
                    write(json(dartPropName))
                    write(" == null ? null : ")
                }
                write(t.dartClassName)
                if (c is EnumNode) {
                    write(".of(")
                } else {
                    write(".fromJson(")
                }
                write(json(dartPropName))
                write(")")
                if (!prop.type.nullable)
                    if (c is EnumNode) {
                        write("!")
                    }
            }
        } else {
            when (prop.type.mainType) {
                "List" -> {
                    newLine()
                    indent {
                        write("(")
                        write(json(dartPropName))
                        writeln(" as List<dynamic>)")
                        indent {
                            write(".map((e) => ")
                            dartType(prop.type.subTypes[0], onDartType = {
                                write("e as ")
                                write(it)
                            }) { t, c ->
                                write(t.dartClassName)
                                if (c is EnumNode) {
                                    write(".of(e)")
                                } else {
                                    write(".fromJson(e)")
                                }
                            }
                            writeln(")")
                            write(".toList()")
                        }
                    }
                }

                else -> TODO("unsupported type ${prop.type.mainType}")
            }
        }

        write(",")
    }

    private fun IndentWriter.writeFromJson(c: ClassNode) {
        write("factory ")
        write(c.dartName)
        write(".fromJson(JsonObject json) => ")
        write(c.dartName)
        writeln("(")
        c.properties.forEach {
            indent {
                writeFromJsonItem(it)
                newLine()
            }
        }
        writeln(");")
    }

    private fun IndentWriter.writeToJsonItem(prop: PropertyNode) {
        if (prop.type.nullable) {
            write("if (")
            write(prop.name)
            write(" != null) ")
        }
        write(prop.name.dartString)
        write(": ")

        if (prop.type.subTypes.isEmpty()) {
            dartType(prop.type.mainType, onDartType = {
                write(prop.name)
            }) { t, c ->
                write(prop.name)
                if (c is EnumNode) {
                    write(".name")
                } else {
                    write("?.toJson()")
                }
            }
        } else {
            write(prop.name)
            write(".map((e) => ")
            dartType(prop.type.subTypes[0], onDartType = {
                write("e")
            }) { t, c ->
                if (c is EnumNode) {
                    write("e.name")
                } else {
                    write("e.toJson()")
                }
            }
            write(").toList()")
        }
        write(",")
    }

    private fun IndentWriter.writeToJson(c: ClassNode) {
        writeln("JsonObject toJson() => {")
        c.properties.forEach {
            indent {
                writeToJsonItem(it)
                newLine()
            }
        }
        writeln("};")
    }

    protected open fun writeUtils(out: IndentWriter, c: ClassNode) {
        out.writeFromJson(c)
        out.newLine()
        out.writeToJson(c)
    }

    override fun write(out: IndentWriter, c: ClassNode) = with(out) {
        writeDoc(c.docs)
        writeBreakBlock(type, " ", c.dartName, " ") {
            indent {
                writeProperties(this, c)
                newLine()
                writeConstructor(this, c)
                newLine()
                writeUtils(this, c)
            }
        }
    }
}