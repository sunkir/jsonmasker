package dev.sunkir.utils.jsonmasker.util

import dev.sunkir.utils.jsonmasker.annotation.Masked
import dev.sunkir.utils.jsonmasker.annotation.PartialMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class PartialMaskTest {

    @Test
    fun `test maskObject with PartialMask annotations`(softly: SoftAssertions) {
        @Masked
        class PhoneUser(
            @param:PartialMask(pattern = "***cccccccc***")
            val phone: String,
            val name: String
        )

        val user = PhoneUser("12345678901234", "John Doe")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["phone"].asText()).`as` { "Phone" }.isEqualTo("***45678901***")
        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("John Doe")
    }

    @Test
    fun `test PartialMask with custom mask character`(softly: SoftAssertions) {
        @Masked
        class CustomMaskUser(
            @param:PartialMask(pattern = "**cc**", mask = "#")
            val secret: String
        )

        val user = CustomMaskUser("123456")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("##34##")
    }

    @Test
    fun `test PartialMask with default mask value`(softly: SoftAssertions) {
        @Masked
        class DefaultMaskUser(
            @param:PartialMask(pattern = "**cccc")
            val account: String
        )

        val user = DefaultMaskUser("123456")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["account"].asText()).`as` { "Account" }.isEqualTo("**3456")
    }

    @Test
    fun `test PartialMask with null value`(softly: SoftAssertions) {
        @Masked
        class NullableUser(
            @param:PartialMask(pattern = "****")
            val secret: String?
        )

        val user = NullableUser(null)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].isNull).`as` { "Secret" }.isTrue()
    }

    @Test
    fun `test PartialMask with different types`(softly: SoftAssertions) {
        @Masked
        class TypeUser(
            @param:PartialMask(pattern = "*c*")
            val code: Int,
            @param:PartialMask(pattern = "*cccc")
            val active: Boolean
        )

        val user = TypeUser(123, true)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["code"].asText()).`as` { "Code" }.isEqualTo("*2*")
        softly.assertThat(sut["active"].asText()).`as` { "Active" }.isEqualTo("*rue")
    }

    @Test
    fun `test PartialMask with empty string`(softly: SoftAssertions) {
        @Masked
        class EmptyUser(
            @param:PartialMask(pattern = "***")
            val secret: String
        )

        val user = EmptyUser("")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("")
    }

    @Test
    fun `test PartialMask with no pattern`(softly: SoftAssertions) {
        @Masked
        class NoPatternUser(
            @param:PartialMask
            val secret: String
        )

        val user = NoPatternUser("12345")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("12345")
    }
}
