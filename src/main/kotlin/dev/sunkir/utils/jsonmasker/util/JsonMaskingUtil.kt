package dev.sunkir.utils.jsonmasker.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.sunkir.utils.jsonmasker.jackson.MaskingJacksonModule
import dev.sunkir.utils.jsonmasker.strategy.MaskingStrategyFactory


/**
 * Utility for masking sensitive data in objects and JSON strings.
 * Uses [MaskingJacksonModule] for automatic masking of POJO objects.
 *
 * @property objectMapper Configured [ObjectMapper] instance for JSON processing.
 */
open class JsonMaskingUtil(val objectMapper: ObjectMapper = defaultMapper) {

    init {
        objectMapper.registerModule(MaskingJacksonModule())
    }

    /**
     * Serializes an object to JSON without applying any masking.
     *
     * @param obj The object to serialize.
     * @return JSON string.
     */
    fun toJson(obj: Any?): String = maskObject(obj, mask = false)

    /**
     * Serializes an object to JSON with masking based on annotations.
     * The class must be marked with [@Masked], and its fields with specific masking annotations.
     *
     * @param obj The object to mask and serialize.
     * @return JSON string with masked fields.
     */
    fun toMaskedJson(obj: Any?): String = maskObject(obj, mask = true)

    /**
     * Masks an object based on annotations.
     *
     * @param obj The object to mask.
     * @param mask Whether to apply masking.
     * @return JSON string.
     */
    private fun maskObject(obj: Any?, mask: Boolean): String {
        return if (!mask) {
            objectMapper.writer()
                .withAttribute(MaskingJacksonModule.SUPPRESS_MASKING, true)
                .writeValueAsString(obj)
        } else {
            objectMapper.writeValueAsString(obj)
        }
    }

    /**
     * Masks a JSON string based on the provided key configuration.
     *
     * @param json The source JSON string.
     * @param config Masking configuration for keys [MaskingConfiguration].
     * @param mask Whether to apply masking (default is true).
     * @return Masked JSON string.
     */
    fun maskJsonString(json: String, config: MaskingConfiguration, mask: Boolean = true): String {
        if (!mask) return json
        val rootNode = objectMapper.readTree(json)
        maskNodeByKey(rootNode, config)
        return objectMapper.writeValueAsString(rootNode)
    }

    /**
     * Recursively traverses [JsonNode] and masks values by keys according to the configuration.
     */
    private fun maskNodeByKey(node: JsonNode, config: MaskingConfiguration) {
        when (node) {
            is ObjectNode -> node.fields().forEach { (fieldName, childNode) ->
                config.getConfigForKey(fieldName)?.let { keyConfig ->
                    if (childNode.isValueNode && !childNode.isNull) {
                        val originalValue = childNode.asText()
                        val maskedValue = MaskingStrategyFactory.getExecutorByClass(keyConfig.strategyClass)
                            .mask(originalValue, keyConfig.pattern, keyConfig.mask)

                        node.put(fieldName, maskedValue)
                    } else {
                        maskNodeByKey(childNode, config)
                    }
                } ?: maskNodeByKey(childNode, config)
            }

            is ArrayNode -> node.forEach { maskNodeByKey(it, config) }
        }
    }

    companion object {
        @JvmStatic
        val defaultMapper: ObjectMapper by lazy {
            jacksonObjectMapper()
        }
    }
}

