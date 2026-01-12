package dev.sunkir.utils.jsonmasker.util

import dev.sunkir.utils.jsonmasker.annotation.FullMask
import dev.sunkir.utils.jsonmasker.annotation.Masked
import dev.sunkir.utils.jsonmasker.annotation.SymbolMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(SoftAssertionsExtension::class)
class ExternalJsonMaskingTest {

    private val util = TestUtil.externalJsonTestUtil

    @Test
    fun `test jsonMask with external objectMapper`(softly: SoftAssertions) {
        @Masked
        class SimpleUser(
            val name: String,
            @param:FullMask(mask = "[CONFIDENTIAL]")
            val email: String,
            val age: Int,
        )

        val user = SimpleUser("John Doe", "john@example.com", 30)
        val json = util.toMaskedJson(user)
        val sut = util.objectMapper.readTree(json)
        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("John Doe")
        softly.assertThat(sut["email"].asText()).`as` { "Email" }.isEqualTo("[CONFIDENTIAL]")
        softly.assertThat(sut["age"].asInt()).`as` { "Age" }.isEqualTo(30)
    }
}
