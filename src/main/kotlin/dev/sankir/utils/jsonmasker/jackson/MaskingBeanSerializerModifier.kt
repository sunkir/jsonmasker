package dev.sankir.utils.jsonmasker.jackson

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import dev.sankir.utils.jsonmasker.annotation.Masked
import dev.sankir.utils.jsonmasker.annotation.MaskedField.Companion.ALLOWED_ANNOTATIONS
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses

/**
 * Serialization modifier that looks for masking annotations.
 */
internal class MaskingBeanSerializerModifier : BeanSerializerModifier() {
    private val annotationCache = ConcurrentHashMap<String, Optional<Annotation>>()

    override fun changeProperties(
        config: SerializationConfig,
        beanDescription: BeanDescription,
        beanProperties: MutableList<BeanPropertyWriter>,
    ): MutableList<BeanPropertyWriter> =
        beanDescription.beanClass
            .takeIf { isMaskedInherited(it) }
            ?.let { beanClass ->
                beanProperties.onEach { writer ->
                    findMaskingAnnotationWithCache(writer, beanClass)
                        ?.let { writer.assignSerializer(MaskingSerializer(it)) }
                }
            } ?: beanProperties

    private fun isMaskedInherited(beanClass: Class<*>): Boolean =
        beanClass.kotlin.let { kClass ->
            kClass.allSuperclasses.plus(kClass).any { it.java.isAnnotationPresent(Masked::class.java) }
        }

    private fun findMaskingAnnotationWithCache(writer: BeanPropertyWriter, beanClass: Class<*>): Annotation? =
        annotationCache.computeIfAbsent("${beanClass.name}.${writer.name}") {
            Optional.ofNullable(findMaskingAnnotation(writer, beanClass))
        }.orElse(null)

    private fun findMaskingAnnotation(writer: BeanPropertyWriter, beanClass: Class<*>): Annotation? =
        ALLOWED_ANNOTATIONS
            .firstNotNullOfOrNull { annClass -> writer.getAnnotation(annClass.java) }
            ?: findInKHierarchy(beanClass.kotlin, writer.name, ALLOWED_ANNOTATIONS)

    private fun findInKHierarchy(
        kClass: KClass<*>,
        propertyName: String,
        annotationClasses: Array<KClass<out Annotation>>,
    ): Annotation? =
        kClass.memberProperties
            .find { it.name == propertyName }
            ?.annotations
            ?.find { ann -> annotationClasses.any { it == ann.annotationClass } }
            ?: kClass.constructors
                .asSequence()
                .flatMap { it.parameters }
                .find { it.name == propertyName }
                ?.annotations
                ?.find { ann -> annotationClasses.any { it == ann.annotationClass } }
            ?: kClass.superclasses
                .asSequence()
                .mapNotNull { findInKHierarchy(it, propertyName, annotationClasses) }
                .firstOrNull()
}