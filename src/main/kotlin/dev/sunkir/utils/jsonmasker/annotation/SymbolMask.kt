package dev.sunkir.utils.jsonmasker.annotation

/**
 * Strategy for full replacement of each character with a mask character.
 *
 * @property mask Mask character (default is '*').
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MaskedField
annotation class SymbolMask(val mask: String = MaskedField.DEFAULT_STRING_MASK)
