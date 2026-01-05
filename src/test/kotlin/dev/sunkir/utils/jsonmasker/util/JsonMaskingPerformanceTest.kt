package dev.sunkir.utils.jsonmasker.util

import dev.sunkir.utils.jsonmasker.annotation.SymbolMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.system.measureTimeMillis

@ExtendWith(SoftAssertionsExtension::class)
class JsonMaskingPerformanceTest {

    private val masker = TestUtil.jsonNativeTestUtil

    @Test
    fun `test performance with large json`(softly: SoftAssertions) {
        val itemCount = 10000
        val json = buildString {
            append("[")
            for (i in 0 until itemCount) {
                append("""{"id": $i, "name": "User $i", "secret": "secret-value-$i", "nested": {"key": "val-$i"}}""")
                if (i < itemCount - 1) append(",")
            }
            append("]")
        }

        val config = MaskingConfiguration().apply {
            addKeyConfig("secret", KeyMaskingConfig(strategyClass = SymbolMask::class))
            addKeyConfig("name", KeyMaskingConfig(strategyClass = SymbolMask::class))
        }

        val time = measureTimeMillis {
            val sut = masker.maskJsonString(json, config)
            softly.assertThat(sut).isNotNull
        }

        println("[DEBUG_LOG] Execution time for $itemCount items: $time ms")

        softly.assertThat(time).`as` { "Execution time" }.isLessThan(2000)
    }
}
