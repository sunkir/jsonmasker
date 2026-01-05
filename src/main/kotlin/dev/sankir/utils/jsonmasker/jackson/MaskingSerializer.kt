package dev.sankir.utils.jsonmasker.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import dev.sankir.utils.jsonmasker.annotation.*
import dev.sankir.utils.jsonmasker.strategy.MaskingStrategyFactory

/**
 * Serializer that performs value masking.
 */
internal class MaskingSerializer(maskingAnnotation: Annotation) : JsonSerializer<Any>() {

    private val executor = MaskingStrategyFactory.getExecutor(maskingAnnotation)
    private val pattern = maskingAnnotation.let { ann ->
        when (ann) {
            is RegexpMask -> ann.pattern
            is PartialMask -> ann.pattern
            else -> ""
        }
    }
    private val mask = maskingAnnotation.let { ann ->
        when (ann) {
            is SymbolMask -> ann.mask
            is RegexpMask -> ann.mask
            is FullMask -> ann.mask
            is PartialMask -> ann.mask
            else -> MaskedField.DEFAULT_CHAR_MASK
        }
    }

    override fun serialize(value: Any?, gen: JsonGenerator, serializers: SerializerProvider) {
        value?.let { v ->
            when {
                serializers.getAttribute(MaskingJacksonModule.SUPPRESS_MASKING) == true ->
                    serializers.defaultSerializeValue(v, gen)

                else -> {
                    val maskedValue = executor.mask(v, pattern, mask.toString())
                    gen.writeString(maskedValue)
                }
            }
        } ?: gen.writeNull()
    }
}