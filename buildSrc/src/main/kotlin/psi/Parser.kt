package psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * 解析器
 * @author YJL
 */
interface Parser<Psi : PsiElement, R> {
    /**
     * 解析
     */
    fun parse(psi: Psi): R

    companion object {

        /**
         * 获取文档注释
         */
        fun preDoc(thisPsi: PsiElement, config: List<KDocSection>.() -> Unit): KDoc? {
            return (thisPsi.firstChild as? KDoc)?.apply {
                val rs = mutableListOf<KDocSection>()
                for (child in thisPsi.children[0].children) {
                    if (child !is KDocSection)
                        continue
                    if (child.getChildrenOfType<KDocTag>().isNotEmpty())
                        break
                    if (child.getContent().isNotBlank())
                        rs.add(child)
                }
                if (rs.isEmpty())
                    return@apply
                rs.apply(config)
            }
        }

        /**
         * 获取文档Tag
         */
        fun forEachDocTag(thisPsi: PsiElement, config: KDocSection.() -> Unit): KDoc? {
            return (thisPsi.firstChild as? KDoc)?.apply {
                val rs = children.filter {
                    it is KDocSection && it.getChildrenOfType<KDocTag>().isNotEmpty()
                } as List<KDocSection>
                rs.forEach(config)
            }
        }
    }

}
