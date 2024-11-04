package psi

import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * @author YJL
 */
class TypeNode(
    name: String,
    val nullable: Boolean = false
) : BaseNode(name) {

    val isSub = name.contains(".")

    companion object {
        fun of(type: KtTypeReference): TypeNode {
            var t = type.text
            val nullable = t.endsWith("?").also {
                if (it)
                    t = t.removeSuffix("?")
            }
            return TypeNode(t, nullable)
        }
    }
}