package dev.sunkir.utils.jsonmasker.strategy

import dev.sunkir.utils.jsonmasker.event.MaskingEvent
import dev.sunkir.utils.jsonmasker.event.MaskingEventDispatcher
import dev.sunkir.utils.jsonmasker.event.MaskingEventType
import java.util.concurrent.ConcurrentHashMap

/**
 * Masking strategy based on regular expressions.
 * Allows replacing parts of a string matching a pattern with a mask (supports capture groups).
 */
internal class RegexpMaskStrategy : MaskingStrategyExecutor {
    private val regexCache = ConcurrentHashMap<String, Regex>()

    override fun mask(value: Any, pattern: String, mask: String): String =
        value.toString()
            .let { strValue ->
                when {
                    pattern.isEmpty() -> strValue
                    else ->
                        runCatching {
                            regexCache
                                .computeIfAbsent(pattern) { Regex(it) }
                                .let { regex -> strValue.replace(regex, mask) }
                        }.onFailure {
                            MaskingEventDispatcher.dispatch(
                                MaskingEvent(
                                    type = MaskingEventType.ERROR,
                                    message = "Masking failed for pattern: $pattern",
                                    strategy = this::class,
                                    originalValue = strValue,
                                    throwable = it
                                )
                            )
                        }.getOrDefault(strValue)
                }
            }
}
