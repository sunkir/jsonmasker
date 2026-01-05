package dev.sunkir.utils.jsonmasker.annotation

/**
 * Strategy for replacing the entire value with a mask string.
 *
 * @property mask Mask string (default is [DEFAULT_MASK]).
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MaskedField
annotation class FullMask(val mask: String = DEFAULT_MASK){
    
    companion object{
        const val DEFAULT_MASK = "[MASKED]"
    }
}
