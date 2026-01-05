package dev.sankir.utils.jsonmasker.annotation

/**
 * Strategy for masking based on a regular expression.
 *
 * @property pattern Regular expression pattern.
 * @property mask Mask string (can contain references to capture groups $1, $2...).
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MaskedField
annotation class RegexpMask(val pattern: String, val mask: String = MaskedField.DEFAULT_STRING_MASK)
