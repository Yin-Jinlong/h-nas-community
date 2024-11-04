package task

import env.KotlinEnvironment
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import java.io.File

/**
 * @author YJL
 */
abstract class AbstractKotlin2DTSTask : DefaultTask(), Disposable {

    @InputFiles
    val sourceDirs: ListProperty<Directory>

    @Input
    val links: MutableList<File> = mutableListOf()

    @OutputFile
    val outputFile: RegularFileProperty

    init {
        val of = project.objects
        sourceDirs = of.listProperty(Directory::class.java)
        outputFile = of.fileProperty()
    }

    protected fun createEnv() = KotlinEnvironment.create(this)

    override fun dispose() = Unit

    abstract fun preRun()

    abstract fun run()

    @TaskAction
    fun runTask() {
        preRun()
        run()
    }
}