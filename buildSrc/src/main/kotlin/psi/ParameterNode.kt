package psi

import org.jetbrains.kotlin.psi.KtParameter

/**
 * @author YJL
 */
class ParameterNode(
    name: String,
    val type: TypeNode,
    val isProperty: Boolean = false
) : BaseNode(name) {

    companion object : Parser<KtParameter, ParameterNode> {
        override fun parse(psi: KtParameter) = ParameterNode(
            psi.name!!,
            TypeNode.of(psi.typeReference!!),
            psi.hasValOrVar()
        ).apply {
            Parser.preDoc(psi) {
                docs.addAll(map { it.getContent().trim() })
            }
        }
    }
}