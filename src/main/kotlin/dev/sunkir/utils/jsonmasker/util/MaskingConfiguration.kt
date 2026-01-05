package dev.sunkir.utils.jsonmasker.util

import dev.sunkir.utils.jsonmasker.annotation.MaskedField
import dev.sunkir.utils.jsonmasker.annotation.SymbolMask
import kotlin.reflect.KClass

/**
 * Masking configuration for a specific JSON key.
 *
 * @property strategyClass The strategy annotation class (e.g., SymbolMask::class).
 * @property pattern Pattern for searching (for REGEXP).
 * @property mask Mask character or string.
 */
data class KeyMaskingConfig(
    val strategyClass: KClass<*> = SymbolMask::class,
    val pattern: String = "",
    val mask: String = MaskedField.DEFAULT_STRING_MASK
)

/**
 * Global masking configuration for JSON keys.
 * Allows defining masking rules for specific fields or fields matching a wildcard pattern.
 */
class MaskingConfiguration {
    private val keyConfigs = mutableMapOf<String, KeyMaskingConfig>()
    private val wildcardConfigs = mutableListOf<Pair<Regex, KeyMaskingConfig>>()

    /**
     * Adds configuration for a key or pattern.
     *
     * @param keyPattern Key or pattern (e.g., "password" or "*_id").
     * @param config Masking configuration.
     */
    fun addKeyConfig(keyPattern: String, config: KeyMaskingConfig) {
        if (keyPattern.contains(MaskedField.DEFAULT_STRING_MASK)) {
            val regex = keyPattern.replace(".", "\\.").replace(MaskedField.DEFAULT_STRING_MASK, ".*").toRegex()
            wildcardConfigs.add(regex to config)
        } else {
            keyConfigs[keyPattern] = config
        }
    }

    /**
     * Returns the configuration for the specified key if found.
     * Supports exact matches and wildcard patterns (using '*').
     *
     * @param key The JSON field name.
     * @return [KeyMaskingConfig] or null if no rule is found.
     */
    fun getConfigForKey(key: String): KeyMaskingConfig? {
        return keyConfigs[key] ?: wildcardConfigs.find { it.first.matches(key) }?.second
    }
}
