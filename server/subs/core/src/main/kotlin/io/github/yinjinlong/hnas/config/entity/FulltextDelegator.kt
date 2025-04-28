package io.github.yinjinlong.hnas.config.entity

import io.github.yinjinlong.hnas.annotation.FulltextIndex
import io.github.yinjinlong.hnas.utils.came2under
import jakarta.persistence.Table
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ResourceLoaderAware
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.util.ClassUtils
import org.springframework.util.SystemPropertyUtils
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

/**
 * @author YJL
 */
@Configuration
@Transactional
class FulltextDelegator(
    val jdbcTemplate: JdbcTemplate
) : ApplicationContextAware, ResourceLoaderAware, CommandLineRunner ,ResultSetExtractor<Boolean>{

    lateinit var loader: ResourceLoader
    lateinit var resolver: ResourcePatternResolver
    lateinit var metadataReaderFactory: MetadataReaderFactory
    lateinit var context: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        loader = resourceLoader
        resolver = ResourcePatternUtils.getResourcePatternResolver(loader)
        metadataReaderFactory = CachingMetadataReaderFactory(loader)
    }

    private fun scan(pkg: String): Set<KClass<*>> = HashSet<KClass<*>>().apply {
        val scanPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(
                    SystemPropertyUtils.resolvePlaceholders(pkg)
                ) +
                "/**/*.class"

        for (res in resolver.getResources(scanPath)) {
            if (!res.isReadable) continue
            val meta = metadataReaderFactory.getMetadataReader(res).classMetadata
            if (meta.isConcrete) {
                add(Class.forName(meta.className).kotlin)
            }
        }
    }

    private val KClass<*>.table: Table?
        get() = AnnotationUtils.getAnnotation(java, Table::class.java)

    private val KProperty<*>.fulltextIndex: FulltextIndex?
        get() = annotations.find { it is FulltextIndex } as? FulltextIndex

    private fun KClass<*>.fullTexts(): List<Pair<KProperty<*>, FulltextIndex>> =
        memberProperties.mapNotNull { prop -> prop.fulltextIndex?.let { prop to it } }

    private fun doCreate(clazz: KClass<*>) {
        val tableName = (clazz.table?.name ?: return).ifEmpty {
            clazz.simpleName?.came2under ?: return
        }
        clazz.fullTexts().forEach {
            val nameUnderline = it.first.name.came2under
            val name = it.second.name.ifEmpty { nameUnderline }
            if (!exists(tableName, nameUnderline))
                create(tableName, nameUnderline, name)
        }
    }

    private fun exists(table: String, column: String): Boolean {
        return jdbcTemplate.query("show index from $table where column_name=?", this, column) ?: false
    }

    @Suppress("SqlSourceToSinkFlow")
    fun create(table: String, column: String, name: String) {
        jdbcTemplate.execute("create fulltext index $name on $table ($column) with parser ngram")
    }

    override fun extractData(rs: ResultSet): Boolean? {
        return rs.next()
    }

    override fun run(vararg args: String) {
        scan("io.github.yinjinlong.hnas.entity").forEach {
            doCreate(it)
        }
    }
}