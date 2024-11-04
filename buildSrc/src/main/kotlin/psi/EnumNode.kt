package psi

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.kdoc.psi.impl.KDocImpl
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtEnumEntry

/**
 * @author YJL
 */
class EnumNode(
    name: String,
) : ClassNode(name) {
    val values: MutableList<String> = mutableListOf()

    companion object : Parser<KtClass, EnumNode> {
        override fun parse(psi: KtClass): EnumNode {
            val cn = ClassNode.parse(psi)
            return EnumNode(cn.name).apply {
                properties.addAll(cn.properties)
                psi.body?.children?.filter { it is KtEnumEntry }?.forEach {
                    var n: PsiElement = it.firstChild
                    while ((n as? LeafPsiElement?)?.elementType != KtTokens.IDENTIFIER) {
                        n = n.nextSibling
                            ?: throw IllegalArgumentException("enum entry name not found")
                    }
                    values.add(n.text)
                }
            }
        }
    }
}