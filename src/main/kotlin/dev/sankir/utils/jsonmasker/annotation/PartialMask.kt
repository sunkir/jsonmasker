package dev.sankir.utils.jsonmasker.annotation

/**
 * Strategy for character-by-character masking of partial data.
 * Allows masking symbols based on a pattern.
 * In the pattern, the '*' symbol means the corresponding character in the source string should be masked.
 * Any other character in the pattern (usually 'c') means the character should be preserved.
 *
 * @property mask Mask character (default is '*').
 * @property pattern Masking pattern (e.g., "***cc*cc").
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MaskedField
annotation class PartialMask(
    val mask: String = MaskedField.DEFAULT_STRING_MASK,
    val pattern: String = ""
)
