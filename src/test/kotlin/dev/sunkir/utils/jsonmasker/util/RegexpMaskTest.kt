package dev.sunkir.utils.jsonmasker.util

import dev.sunkir.utils.jsonmasker.annotation.Masked
import dev.sunkir.utils.jsonmasker.annotation.RegexpMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class RegexpMaskTest {

    @Test
    fun `test maskObject with RegexpMask annotations`(softly: SoftAssertions) {
        @Masked
        class SimpleUser(
            @param:RegexpMask(pattern = "\\d{4}", mask = "****")
            val cardSuffix: String,
            val name: String
        )

        val user = SimpleUser("12345678", "John")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["cardSuffix"].asText()).`as` { "CardSuffix" }.isEqualTo("********")
        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("John")
    }

    @Test
    fun `test RegexpMask with capture groups`(softly: SoftAssertions) {
        @Masked
        class CaptureGroupUser(
            @param:RegexpMask(pattern = "(\\d{2})\\d+(\\d{2})", mask = "$1****$2")
            val phone: String
        )

        val user = CaptureGroupUser("79991234567")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["phone"].asText()).`as` { "Phone" }.isEqualTo("79****67")
    }

    @Test
    fun `test RegexpMask with email from readme`(softly: SoftAssertions) {
        @Masked
        class EmailUser(
            @param:RegexpMask(pattern = "(?<=.).+(?=.@)", mask = "********")
            val email: String
        )

        val user = EmailUser("mytestmail@gmail.com")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["email"].asText()).`as` { "Email" }.isEqualTo("m********l@gmail.com")
    }

    @Test
    fun `test RegexpMask with null value`(softly: SoftAssertions) {
        @Masked
        class NullableUser(
            @param:RegexpMask(pattern = ".*", mask = "HIDDEN")
            val secret: String?
        )

        val user = NullableUser(null)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].isNull).`as` { "Secret" }.isTrue()
    }

    @Test
    fun `test RegexpMask with invalid regex`(softly: SoftAssertions) {
        @Masked
        class InvalidRegexUser(
            @param:RegexpMask(pattern = "[", mask = "ERROR")
            val value: String
        )

        val user = InvalidRegexUser("test")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["value"].asText()).`as` { "Value" }.isEqualTo("test")
    }

    @Test
    fun `test RegexpMask with empty string`(softly: SoftAssertions) {
        @Masked
        class EmptyUser(
            @param:RegexpMask(pattern = ".*", mask = "EMPTY")
            val secret: String
        )

        val user = EmptyUser("")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("EMPTY")
    }
}
