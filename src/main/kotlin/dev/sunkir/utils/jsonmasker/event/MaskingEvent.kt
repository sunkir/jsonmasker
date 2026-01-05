package dev.sunkir.utils.jsonmasker.event

import kotlin.reflect.KClass

/**
 * Represents a masking event.
 *
 * @property type The type of the event (ERROR).
 * @property message Descriptive message.
 * @property fieldName The name of the field being masked (if applicable).
 * @property strategy Strategy class used.
 * @property originalValue Value before masking (caution: may contain sensitive data).
 * @property maskedValue Value after masking.
 * @property throwable Error that occurred during masking.
 */
data class MaskingEvent(
    val type: MaskingEventType,
    val message: String,
    val fieldName: String? = null,
    val strategy: KClass<*>? = null,
    val originalValue: Any? = null,
    val maskedValue: String? = null,
    val throwable: Throwable? = null
)

enum class MaskingEventType {
    ERROR
}

/**
 * Listener interface for masking events.
 */
fun interface MaskingEventListener {
    fun onEvent(event: MaskingEvent)
}

/**
 * Global dispatcher for masking events.
 */
object MaskingEventDispatcher {
    private val listeners = mutableListOf<MaskingEventListener>()

    /**
     * Registers a new listener.
     */
    fun addListener(listener: MaskingEventListener) {
        listeners.add(listener)
    }

    /**
     * Removes a listener.
     */
    fun removeListener(listener: MaskingEventListener) {
        listeners.remove(listener)
    }

    /**
     * Dispatches an event to all registered listeners.
     */
    fun dispatch(event: MaskingEvent) {
        listeners.forEach { it.onEvent(event) }
    }
}
