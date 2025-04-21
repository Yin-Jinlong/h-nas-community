package psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtEnumEntry

/**
 * @author YJL
 */
class EnumNode(
    pre: String,
    name: String,
) : ClassNode(pre,name) {
    val values: MutableList<String> = mutableListOf()

    companion object : Parser<KtClass, EnumNode> {
        override fun parse(pre: String, psi: KtClass): EnumNode {
            val cn = ClassNode.parse(pre, psi)
            return EnumNode(pre,cn.name).apply {
                properties.addAll(cn.properties)
                psi.body?.children?.filterIsInstance<KtEnumEntry>()?.forEach {
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