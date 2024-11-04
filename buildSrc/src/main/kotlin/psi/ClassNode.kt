package psi

import org.jetbrains.kotlin.psi.KtClass

/**
 * @author YJL
 */
open class ClassNode(
    name: String,
) : BaseNode(name) {

    val properties = mutableListOf<PropertyNode>()

    fun getProperty(name: String) = properties.find { it.name == name }

    companion object : Parser<KtClass, ClassNode> {
        override fun parse(psi: KtClass) = ClassNode(psi.name!!).apply {
            psi.primaryConstructorParameters.forEach {
                val parm = ParameterNode.parse(it)
                if (parm.isProperty)
                    properties.add(PropertyNode.of(parm))
            }
            Parser.preDoc(psi) {
                docs.addAll(map { it.getContent().trim() })
            }
            Parser.forEachDocTag(psi) {
                val docs = findTagByName("property") ?: return@forEachDocTag
                val pn = docs.getSubjectName() ?: return@forEachDocTag
                val p = getProperty(pn) ?: return@forEachDocTag
                if (p.docs.isEmpty())
                    p.docs.add(docs.getContent())
            }
        }
    }
}