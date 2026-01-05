package dev.sunkir.utils.jsonmasker.strategy

import dev.sunkir.utils.jsonmasker.annotation.FullMask.Companion.DEFAULT_MASK

/**
 * Strategy for replacing the entire value with a placeholder string.
 * Example: "secret" -> "[DEFAULT_MASK]"
 */
internal class FullMaskStrategy : MaskingStrategyExecutor {

    override fun mask(value: Any, pattern: String, mask: String) =
        mask.ifEmpty { DEFAULT_MASK }
}
