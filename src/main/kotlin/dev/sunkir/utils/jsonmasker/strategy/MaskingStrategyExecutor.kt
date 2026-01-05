package dev.sunkir.utils.jsonmasker.strategy

/**
 * Interface for implementing a data masking strategy.
 */
fun interface MaskingStrategyExecutor {

    /**
     * Performs masking of the provided value.
     *
     * @param value The original value.
     * @param pattern Pattern (e.g., regex or positional pattern), if applicable.
     * @param mask Mask string or character.
     * @return Masked string representation of the value.
     */
    fun mask(value: Any, pattern: String, mask: String): String
}
