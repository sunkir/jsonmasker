package dev.sunkir.utils.jsonmasker.annotation

import java.lang.annotation.Inherited

/**
 * Annotation for marking classes where field masking should be performed.
 * If a class is not marked with this annotation, masking of its fields will not be performed.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class Masked
