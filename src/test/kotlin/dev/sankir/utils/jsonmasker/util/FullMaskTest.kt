package dev.sankir.utils.jsonmasker.util

import dev.sankir.utils.jsonmasker.annotation.FullMask
import dev.sankir.utils.jsonmasker.annotation.Masked
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class FullMaskTest {

    @Test
    fun `test maskObject with FullMask annotations`(softly: SoftAssertions) {
        @Masked
        class SimpleUser(
            val name: String,
            @param:FullMask(mask = "[CONFIDENTIAL]")
            val email: String,
            val age: Int,
        )

        val user = SimpleUser("John Doe", "john@example.com", 30)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)
        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("John Doe")
        softly.assertThat(sut["email"].asText()).`as` { "Email" }.isEqualTo("[CONFIDENTIAL]")
        softly.assertThat(sut["age"].asInt()).`as` { "Age" }.isEqualTo(30)
    }

    @Test
    fun `test FullMask with default mask value`(softly: SoftAssertions) {
        @Masked
        class DefaultMaskUser(
            @param:FullMask
            val secret: String
        )

        val user = DefaultMaskUser("secret-value")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)
        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("[masked]")
    }

    @Test
    fun `test FullMask with null value`(softly: SoftAssertions) {
        @Masked
        class NullableUser(
            @param:FullMask(mask = "HIDDEN")
            val secret: String?
        )

        val user = NullableUser(null)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)
        softly.assertThat(sut["secret"].isNull).`as` { "Secret" }.isTrue()
    }

    @Test
    fun `test FullMask with different types`(softly: SoftAssertions) {
        @Masked
        class TypeUser(
            @param:FullMask(mask = "0")
            val score: Int,
            @param:FullMask(mask = "false")
            val active: Boolean
        )

        val user = TypeUser(100, true)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)
        softly.assertThat(sut["score"].asText()).`as` { "Score" }.isEqualTo("0")
        softly.assertThat(sut["active"].asText()).`as` { "Active" }.isEqualTo("false")
    }

    @Test
    fun `test FullMask with empty mask value`(softly: SoftAssertions) {
        @Masked
        class EmptyMaskUser(
            @param:FullMask(mask = "")
            val secret: String
        )

        val user = EmptyMaskUser("something")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)
        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("[MASKED]")
    }
}