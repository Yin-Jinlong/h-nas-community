package env

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import psi.ClassNode
import psi.EnumNode
import psi.Node

/**
 * @author YJL
 */
object KotlinEnvironment {

    fun create(disposable: Disposable): KotlinCoreEnvironment {
        return KotlinCoreEnvironment.createForProduction(
            disposable,
            CompilerConfiguration.EMPTY,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
    }

}


private fun PsiElement.forEachPsi(block: PsiElement.() -> Unit) {
    var spi: PsiElement? = firstChild
    while (spi != null) {
        spi.block()
        spi = spi.nextSibling
    }
}

private fun PsiElement.parseNode(pre: String = "", onAdd: Node.(key: String) -> Unit = {}) {
    val node = when (this) {
        is KtClass -> {
            if (isEnum())
                EnumNode.parse(this)
            else {
                ClassNode.parse(this).apply {
                    body?.children?.forEach {
                        it.parseNode("$pre$name.", onAdd)
                    }
                }
            }
        }

        else -> return
    }

    val key = "$pre${node.name}"
    node.onAdd(key)
}


fun KotlinCoreEnvironment.parseNodes(onAdd: Node.(key: String) -> Unit = {}) {
    val psi = PsiManager.getInstance(project)
    getSourceFiles().forEach {
        psi.findFile(it.virtualFile)?.apply {
            forEachPsi {
                parseNode(onAdd = onAdd)
            }
        }
    }
}
