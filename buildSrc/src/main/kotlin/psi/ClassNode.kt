package psi

import org.jetbrains.kotlin.psi.KtClass
import utils.dartClassName

/**
 * @author YJL
 */
open class ClassNode(
    pre: String,
    name: String,
) : BaseNode(name) {

    val properties = mutableListOf<PropertyNode>()

    val fullName = if (pre.isEmpty()) name else "$pre$name"

    val dartName = fullName.dartClassName

    fun getProperty(name: String) = properties.find { it.name == name }

    companion object : Parser<KtClass, ClassNode> {
        override fun parse(pre: String, psi: KtClass) = ClassNode(pre, psi.name!!).apply {
            psi.primaryConstructorParameters.forEach {
                val parm = ParameterNode.parse(pre, it)
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