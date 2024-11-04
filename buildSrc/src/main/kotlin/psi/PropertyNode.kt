package psi

import org.jetbrains.kotlin.psi.KtProperty

/**
 * @author YJL
 */
class PropertyNode(
    name: String,
    val type: TypeNode,
) : BaseNode(name) {

    companion object : Parser<KtProperty, PropertyNode> {
        override fun parse(psi: KtProperty): PropertyNode {
            TODO("Not yet implemented")
        }

        fun of(parm: ParameterNode) = PropertyNode(parm.name, parm.type).apply {
            docs.addAll(parm.docs)
        }

    }
}