package dev.sankir.utils.jsonmasker.util

import dev.sankir.utils.jsonmasker.annotation.Masked
import dev.sankir.utils.jsonmasker.annotation.SymbolMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class InterfaceInheritanceTest {

    @Masked
    interface BaseIdentifiable {
        @SymbolMask
        val name: String
    }

    interface IntermediateIdentifiable : BaseIdentifiable

    class DeepUser(override val name: String) : IntermediateIdentifiable

    @Test
    fun `test masking with Masked annotation on deep interface hierarchy`(softly: SoftAssertions) {
        val user = DeepUser("DeepSecret")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("**********")
    }

    @Masked
    interface SimpleInterface {
        @SymbolMask
        val secret: String
    }

    @Test
    fun `test masking with Masked annotation on direct interface`(softly: SoftAssertions) {
        class SimpleImpl(override val secret: String) : SimpleInterface

        val impl = SimpleImpl("mysecret")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(impl)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("********")
    }
}
