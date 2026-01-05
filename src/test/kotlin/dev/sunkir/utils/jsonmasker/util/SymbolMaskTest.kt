package dev.sunkir.utils.jsonmasker.util

import dev.sunkir.utils.jsonmasker.annotation.Masked
import dev.sunkir.utils.jsonmasker.annotation.SymbolMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class SymbolMaskTest {

    @Test
    fun `test maskObject with SymbolMask annotations`(softly: SoftAssertions) {
        @Masked
        class SimpleUser(
            @param:SymbolMask
            val name: String,
            val age: Int
        )

        val user = SimpleUser("John", 30)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("****")
        softly.assertThat(sut["age"].asInt()).`as` { "Age" }.isEqualTo(30)
    }

    @Test
    fun `test SymbolMask with custom mask character`(softly: SoftAssertions) {
        @Masked
        class CustomMaskUser(
            @param:SymbolMask(mask = "#")
            val secret: String
        )

        val user = CustomMaskUser("12345")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("#####")
    }

    @Test
    fun `test SymbolMask with null value`(softly: SoftAssertions) {
        @Masked
        class NullableUser(
            @param:SymbolMask
            val secret: String?
        )

        val user = NullableUser(null)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].isNull).`as` { "Secret" }.isTrue()
    }

    @Test
    fun `test SymbolMask with different types`(softly: SoftAssertions) {
        @Masked
        class TypeUser(
            @param:SymbolMask
            val code: Int,
            @param:SymbolMask
            val active: Boolean
        )

        val user = TypeUser(123, true)
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["code"].asText()).`as` { "Code" }.isEqualTo("***")
        softly.assertThat(sut["active"].asText()).`as` { "Active" }.isEqualTo("****")
    }

    @Test
    fun `test SymbolMask with empty string`(softly: SoftAssertions) {
        @Masked
        class EmptyUser(
            @param:SymbolMask
            val secret: String
        )

        val user = EmptyUser("")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("")
    }
}
