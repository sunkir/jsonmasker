package dev.sunkir.utils.jsonmasker.strategy

/**
 * Strategy for replacing each character of a string with a mask character.
 * Example: "password" -> "********"
 */
internal class SymbolMaskStrategy : MaskingStrategyExecutor {

    override fun mask(value: Any, pattern: String, mask: String) =
        (mask.firstOrNull() ?: '*').toString().repeat(value.toString().length)
}
