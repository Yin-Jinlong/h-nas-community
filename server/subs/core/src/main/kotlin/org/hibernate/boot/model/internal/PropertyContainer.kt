/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.model.internal

import jakarta.persistence.*
import org.hibernate.AnnotationException
import org.hibernate.annotations.*
import org.hibernate.annotations.Any
import org.hibernate.annotations.Target
import org.hibernate.annotations.common.reflection.XClass
import org.hibernate.annotations.common.reflection.XProperty
import org.hibernate.annotations.common.reflection.java.JavaXMember
import org.hibernate.boot.MappingException
import org.hibernate.boot.jaxb.Origin
import org.hibernate.boot.jaxb.SourceType
import org.hibernate.boot.spi.AccessType
import org.hibernate.internal.util.StringHelper
import org.hibernate.internal.util.collections.CollectionHelper
import java.util.*
import kotlin.reflect.KClass

/**
 * A helper class to keep the `XProperty`s of a class ordered by access type.
 *
 * @author Hardy Ferentschik
 */
@Suppress("unused")
class PropertyContainer(clazz: XClass, entityAtStake: XClass, defaultClassLevelAccessType: AccessType) {
    /**
     * The class for which this container is created.
     */
    private val xClass: XClass
    private val entityAtStake: XClass

    /**
     * Holds the AccessType indicated for use at the class/container-level for cases where persistent attribute
     * did not specify.
     */
    val classLevelAccessType: AccessType

    private val persistentAttributes: List<XProperty>

    init {
        var defaultClassLevelAccessType = defaultClassLevelAccessType
        this.xClass = clazz
        this.entityAtStake = entityAtStake

        if (defaultClassLevelAccessType == AccessType.DEFAULT) {
            // this is effectively what the old code did when AccessType.DEFAULT was passed in
            // to getProperties(AccessType) from AnnotationBinder and InheritanceState
            defaultClassLevelAccessType = AccessType.PROPERTY
        }

        val localClassLevelAccessType = determineLocalClassDefinedAccessStrategy()
        this.classLevelAccessType = if (localClassLevelAccessType != AccessType.DEFAULT)
            localClassLevelAccessType
        else
            defaultClassLevelAccessType
        assert(classLevelAccessType == AccessType.FIELD || classLevelAccessType == AccessType.PROPERTY || classLevelAccessType == AccessType.RECORD)


        val fields: MutableList<XProperty> = xClass.getDeclaredProperties(AccessType.FIELD.type)
        val getters: MutableList<XProperty> = xClass.getDeclaredProperties(AccessType.PROPERTY.type)
        val recordComponents: MutableList<XProperty> = xClass.getDeclaredProperties(AccessType.RECORD.type)

        preFilter(fields, getters, recordComponents)

        val persistentAttributesFromGetters = HashMap<String, XProperty>()
        val persistentAttributesFromComponents = HashMap<String, XProperty>()
        val localAttributeMap = LinkedHashMap<String, XProperty>()

        collectPersistentAttributesUsingLocalAccessType(
            xClass,
            localAttributeMap,
            persistentAttributesFromGetters,
            persistentAttributesFromComponents,
            fields,
            getters,
            recordComponents
        )
        collectPersistentAttributesUsingClassLevelAccessType(
            xClass,
            classLevelAccessType,
            localAttributeMap,
            persistentAttributesFromGetters,
            persistentAttributesFromComponents,
            fields,
            getters,
            recordComponents
        )
        this.persistentAttributes = verifyAndInitializePersistentAttributes(xClass, localAttributeMap)
    }

    private fun MutableList<XProperty>.removeSkip() {
        val propertyIterator: MutableIterator<XProperty> = iterator()
        while (propertyIterator.hasNext()) {
            val property: XProperty = propertyIterator.next()
            if (mustBeSkipped(property)) {
                propertyIterator.remove()
            }
        }
    }

    private fun preFilter(
        fields: MutableList<XProperty>,
        getters: MutableList<XProperty>,
        recordComponents: MutableList<XProperty>
    ) {
        fields.removeSkip()
        getters.removeSkip()
        recordComponents.removeSkip()
    }

    fun getEntityAtStake(): XClass {
        return entityAtStake
    }

    val declaringClass: XClass
        get() = xClass

    fun propertyIterator(): Iterable<XProperty> {
        return persistentAttributes
    }

    private fun determineLocalClassDefinedAccessStrategy(): AccessType {
        val access: Access? = xClass.getAnnotation(Access::class.java)
        return access?.let { AccessType.getAccessStrategy(access.value) } ?: AccessType.DEFAULT
    }

    companion object {
        private fun collectPersistentAttributesUsingLocalAccessType(
            xClass: XClass,
            persistentAttributeMap: MutableMap<String, XProperty>,
            persistentAttributesFromGetters: MutableMap<String, XProperty>,
            persistentAttributesFromComponents: MutableMap<String, XProperty>,
            fields: MutableList<XProperty>,
            getters: MutableList<XProperty>,
            recordComponents: MutableList<XProperty>
        ) {
            // Check fields...

            var propertyIterator: MutableIterator<XProperty> = fields.iterator()
            while (propertyIterator.hasNext()) {
                val xProperty: XProperty = propertyIterator.next()
                val localAccessAnnotation: Access? = xProperty.getAnnotation(
                    Access::class.java
                )
                if (localAccessAnnotation == null
                    || localAccessAnnotation.value != jakarta.persistence.AccessType.FIELD
                ) {
                    continue
                }

                propertyIterator.remove()
                persistentAttributeMap[xProperty.name] = xProperty
            }

            // Check getters...
            propertyIterator = getters.iterator()
            while (propertyIterator.hasNext()) {
                val xProperty: XProperty = propertyIterator.next()
                val localAccessAnnotation: Access? = xProperty.getAnnotation(
                    Access::class.java
                )
                if (localAccessAnnotation == null
                    || localAccessAnnotation.value != jakarta.persistence.AccessType.PROPERTY
                ) {
                    continue
                }

                propertyIterator.remove()

                val name: String = xProperty.name

                check(persistentAttributesFromGetters, name, xClass, xProperty)

                persistentAttributeMap[name] = xProperty
                persistentAttributesFromGetters[name] = xProperty
            }

            // Check record components...
            propertyIterator = recordComponents.iterator()
            while (propertyIterator.hasNext()) {
                val xProperty: XProperty = propertyIterator.next()
                val localAccessAnnotation: Access? = xProperty.getAnnotation(
                    Access::class.java
                )
                if (localAccessAnnotation == null) {
                    continue
                }

                propertyIterator.remove()
                val name: String = xProperty.name
                persistentAttributeMap[name] = xProperty
                persistentAttributesFromComponents[name] = xProperty
            }
        }

        private fun check(
            persistentAttributesFromGetters: MutableMap<String, XProperty>,
            name: String,
            xClass: XClass,
            xProperty: XProperty
        ) {
            // HHH-10242 detect registration of the same property getter twice - eg boolean isId() + UUID getId()
            val previous: XProperty? = persistentAttributesFromGetters[name]
            if (previous != null) {
                throw MappingException(
                    String.format(
                        Locale.getDefault(),
                        "HHH000474: Ambiguous persistent property methods detected on %s; mark one as @Transient: [%s] and [%s]",
                        xClass.name,
                        HCANNHelper.annotatedElementSignature(previous as JavaXMember),
                        HCANNHelper.annotatedElementSignature(xProperty as JavaXMember)
                    ),
                    Origin(SourceType.ANNOTATION, xClass.name)
                )
            }
        }

        private fun collectPersistentAttributesUsingClassLevelAccessType(
            xClass: XClass,
            classLevelAccessType: AccessType,
            persistentAttributeMap: MutableMap<String, XProperty>,
            persistentAttributesFromGetters: MutableMap<String, XProperty>,
            persistentAttributesFromComponents: MutableMap<String, XProperty>,
            fields: List<XProperty>,
            getters: List<XProperty>,
            recordComponents: List<XProperty>
        ) {
            if (classLevelAccessType == AccessType.FIELD) {
                for (field in fields) {
                    val name: String = field.name
                    if (persistentAttributeMap.containsKey(name)) {
                        continue
                    }

                    persistentAttributeMap[name] = field
                }
            } else {
                for (getter in getters) {
                    val name: String = getter.name

                    check(persistentAttributesFromGetters, name, xClass, getter)

                    if (persistentAttributeMap.containsKey(name)) {
                        continue
                    }

                    persistentAttributeMap[getter.name] = getter
                    persistentAttributesFromGetters[name] = getter
                }
                // When a user uses the `property` access strategy for the entity owning an embeddable,
                // we also have to add the attributes for record components,
                // because record classes usually don't have getters, but just the record component accessors
                for (recordComponent in recordComponents) {
                    val name: String = recordComponent.name
                    if (persistentAttributeMap.containsKey(name)) {
                        continue
                    }

                    persistentAttributeMap[name] = recordComponent
                    persistentAttributesFromComponents[name] = recordComponent
                }
            }
        }

        private fun verifyAndInitializePersistentAttributes(
            xClass: XClass,
            localAttributeMap: Map<String, XProperty>
        ): List<XProperty> {
            val output = ArrayList<XProperty>(localAttributeMap.size)
            for (xProperty in localAttributeMap.values) {
                if (!xProperty.isTypeResolved && !discoverTypeWithoutReflection(xClass, xProperty)) {
                    val msg = "Property '" + StringHelper.qualify(xClass.name, xProperty.name) +
                            "' has an unbound type and no explicit target entity (resolve this generics usage issue" +
                            " or set an explicit target attribute with '@OneToMany(target=)' or use an explicit '@Type')"
                    throw AnnotationException(msg)
                }
                output.add(xProperty)
            }
            return CollectionHelper.toSmallList(output)
        }

        private fun <A : Annotation> XProperty.a(cls: KClass<A>, block: A.() -> Boolean): Boolean {
            return isAnnotationPresent(cls.java) && block(getAnnotation(cls.java))
        }

        private fun discoverTypeWithoutReflection(clazz: XClass, property: XProperty): Boolean {
            if (
                property.a(OneToOne::class) { targetEntity != Void.TYPE } ||
                property.a(ManyToOne::class) { targetEntity != Void.TYPE } ||
                property.a(ManyToMany::class) { targetEntity != Void.TYPE } ||
                property.isAnnotationPresent(Any::class.java)
            )
                return true
            if (property.isAnnotationPresent(ManyToAny::class.java)) {
                if (!property.isCollection && !property.isArray) {
                    throw AnnotationException(
                        ("Property '" + StringHelper.qualify(clazz.name, property.name)
                                + "' annotated '@ManyToAny' is neither a collection nor an array")
                    )
                }
                return true
            }

            return property.isAnnotationPresent(Basic::class.java) ||
                    property.isAnnotationPresent(Type::class.java) ||
                    property.isAnnotationPresent(JavaType::class.java) ||
                    property.isAnnotationPresent(JdbcTypeCode::class.java) ||
                    property.isAnnotationPresent(Target::class.java)
        }

        private fun mustBeSkipped(property: XProperty): Boolean {
            //TODO make those hardcoded tests more portable (through the bytecode provider?)
            return property.isAnnotationPresent(Transient::class.java)
                    || "net.sf.cglib.transform.impl.InterceptFieldCallback" == property.type.name
        }
    }
}