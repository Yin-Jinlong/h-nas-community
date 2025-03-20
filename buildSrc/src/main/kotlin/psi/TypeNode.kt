package psi

import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * @author YJL
 */
class TypeNode(
    name: String,
    val mainType: String,
    val subTypes: List<String>,
    val nullable: Boolean = false
) : BaseNode(name) {

    val isSub = name.contains(".")

    companion object {
        fun of(type: KtTypeReference): TypeNode {
            var t = type.text
            var main: String
            val nullable = t.endsWith("?").also {
                if (it) {
                    t = t.removeSuffix("?")
                }
                main = t
            }
            val types = mutableListOf<String>().apply {
                if (!t.matches(".*<.*>".toRegex()))
                    return@apply
                val str = t.substringAfter("<").let {
                    it.substringBeforeLast(">")
                }
                val list = str.split(",")
                for (s in list) {
                    add(s.trim())
                }
                main = t.substringBefore("<")
            }
            return TypeNode(t, main, types, nullable)
        }
    }
}