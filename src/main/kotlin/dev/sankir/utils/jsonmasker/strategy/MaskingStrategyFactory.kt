package dev.sankir.utils.jsonmasker.strategy

import dev.sankir.utils.jsonmasker.annotation.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Factory for getting masking strategy executors.
 * Supports caching of standard and custom executors.
 */
internal object MaskingStrategyFactory {

    private val executorsCache = ConcurrentHashMap<KClass<*>, MaskingStrategyExecutor>()
        .apply {
            put(RegexpMask::class, RegexpMaskStrategy())
            put(SymbolMask::class, SymbolMaskStrategy())
            put(FullMask::class, FullMaskStrategy())
            put(PartialMask::class, PartialMaskStrategy())
        }

    /**
     * Returns a strategy executor based on the class.
     */
    fun getExecutorByClass(executorClass: KClass<*>): MaskingStrategyExecutor =
        executorsCache.getOrPut(executorClass) {
            executorClass.java.getDeclaredConstructor().newInstance() as MaskingStrategyExecutor
        }

    /**
     * Returns a strategy executor based on the annotation.
     */
    fun getExecutor(annotation: Annotation): MaskingStrategyExecutor =
        executorsCache[annotation.annotationClass] ?: throw IllegalArgumentException("No executor for annotation ${annotation.annotationClass}")
}