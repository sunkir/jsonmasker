package dev.sunkir.utils.jsonmasker.jackson

import com.fasterxml.jackson.databind.module.SimpleModule

/**
 * Jackson module for automatic field masking.
 *
 * Example registration in a custom ObjectMapper:
 * ```kotlin
 * val mapper = ObjectMapper().registerModule(MaskingJacksonModule())
 * ```
 */
class MaskingJacksonModule : SimpleModule() {

    init {
        setSerializerModifier(MaskingBeanSerializerModifier())
    }

    companion object {
        /**
         * Attribute key to disable masking in the current serialization context.
         */
        const val SUPPRESS_MASKING = "dev.sunkir.utils.jsonmasker.SUPPRESS_MASKING"
    }
}

