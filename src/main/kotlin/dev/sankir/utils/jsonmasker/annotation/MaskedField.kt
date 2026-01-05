package dev.sankir.utils.jsonmasker.annotation


/**
 * Meta-annotation for marking specific masking strategies.
 * All strategy annotations must be marked with this annotation.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MaskedField {
    companion object {
        const val DEFAULT_CHAR_MASK = '*'
        const val DEFAULT_STRING_MASK = "*"
        val ALLOWED_ANNOTATIONS = arrayOf(
            SymbolMask::class,
            RegexpMask::class,
            FullMask::class,
            PartialMask::class
        )
    }
}
