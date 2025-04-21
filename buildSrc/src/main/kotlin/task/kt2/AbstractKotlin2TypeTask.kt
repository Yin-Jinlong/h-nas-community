package task.kt2

import env.KotlinEnvironment
import env.parseNodes
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import psi.ClassNode
import java.io.File
import java.util.*

/**
 * @author YJL
 */
abstract class AbstractKotlin2TypeTask : DefaultTask(), Disposable {

    @InputFiles
    val sourceDirs: ListProperty<File>

    @Input
    val links: MutableList<File> = mutableListOf()

    /**
     * 链接类要额外输出到公共类的名字，不包含包名
     */
    @Input
    val link2common: MutableSet<String> = mutableSetOf()

    @OutputFile
    val outputFile: RegularFileProperty


    private val env = createEnv()
    private val linkEnv = createEnv()
    private val linkMap = mutableMapOf<String, ClassNode>()
    private val linkCommons = mutableSetOf<ClassNode>()

    init {
        val of = project.objects
        sourceDirs = of.listProperty(File::class.java)
        outputFile = of.fileProperty()
    }

    protected fun createEnv() = KotlinEnvironment.create(this)

    override fun dispose() = Unit

    open fun preRun() {
        linkEnv.addKotlinSourceRoots(links.toList())
        env.addKotlinSourceRoots(sourceDirs.get())
        linkEnv.parseNodes {
            linkMap[it] = this
            if (link2common.contains(this.fullName))
                linkCommons.add(this)
        }
    }

    abstract fun run(classes: Set<ClassNode>)

    protected fun getLinkClass(name: String) = linkMap[name]

    @TaskAction
    fun runTask() {
        preRun()
        run(TreeSet<ClassNode> { a, b ->
            a.dartName.compareTo(b.dartName)
        }.apply {
            addAll(linkCommons)
            env.parseNodes {
                linkMap[it] = this
                add(this)
            }
        })
    }
}