package dev.sankir.utils.jsonmasker.strategy

import dev.sankir.utils.jsonmasker.annotation.MaskedField

/**
 * Strategy for character-by-character masking of partial data based on a pattern.
 * example: 1234567890 -> 123***78**
 */
internal class PartialMaskStrategy : MaskingStrategyExecutor {

    override fun mask(value: Any, pattern: String, mask: String): String =
        value.toString()
            .takeIf { it.isNotEmpty() }
            ?.let { strValue ->
                val maskChar = mask.firstOrNull() ?: MaskedField.DEFAULT_CHAR_MASK
                strValue.mapIndexed { index, c ->
                    val pChar = pattern.getOrNull(index)
                    if (pChar == '*') maskChar else c
                }.joinToString("")
            } ?: ""
}
