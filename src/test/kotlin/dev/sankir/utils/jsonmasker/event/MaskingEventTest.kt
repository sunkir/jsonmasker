package dev.sankir.utils.jsonmasker.event

import dev.sankir.utils.jsonmasker.annotation.Masked
import dev.sankir.utils.jsonmasker.annotation.RegexpMask
import dev.sankir.utils.jsonmasker.util.TestUtil
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class MaskingEventTest {

    private val util = TestUtil.jsonNativeTestUtil
    private val capturedEvents = mutableListOf<MaskingEvent>()
    private val listener = MaskingEventListener { capturedEvents.add(it) }

    @AfterEach
    fun cleanup() {
        MaskingEventDispatcher.removeListener(listener)
        capturedEvents.clear()
    }

    @Test
    fun `test error events for invalid regex`(softly: SoftAssertions) {
        MaskingEventDispatcher.addListener(listener)

        @Masked
        class BadRegexUser(@param:RegexpMask(pattern = "[", mask = "ERR") val value: String)
        
        util.toMaskedJson(BadRegexUser("test"))

        softly.assertThat(capturedEvents).anySatisfy { event ->
            softly.assertThat(event.type).isEqualTo(MaskingEventType.ERROR)
            softly.assertThat(event.message).contains("Masking failed for pattern")
            softly.assertThat(event.throwable).isNotNull
        }
    }
}
