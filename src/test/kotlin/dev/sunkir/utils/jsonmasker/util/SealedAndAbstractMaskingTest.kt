package dev.sunkir.utils.jsonmasker.util

import dev.sunkir.utils.jsonmasker.annotation.FullMask
import dev.sunkir.utils.jsonmasker.annotation.Masked
import dev.sunkir.utils.jsonmasker.annotation.SymbolMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class SealedAndAbstractMaskingTest {

    @Masked
    abstract class BaseAbstract(
        @param:SymbolMask
        open val name: String
    )

    class AbstractImpl(name: String, val email: String) : BaseAbstract(name)

    @Test
    fun `test masking with abstract class`(softly: SoftAssertions) {
        val obj = AbstractImpl("John", "john@example.com")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(obj)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("****")
        softly.assertThat(sut["email"].asText()).`as` { "Email" }.isEqualTo("john@example.com")
    }

    @Masked
    sealed class MaskedSealed {
        data class MaskedChild(
            @param:SymbolMask
            val secret: String
        ) : MaskedSealed()

        data class UnmaskedChild(
            val value: String
        ) : MaskedSealed()
    }

    @Test
    fun `test masking with sealed class hierarchy`(softly: SoftAssertions) {
        val masked = MaskedSealed.MaskedChild("mysecret")
        val unmasked = MaskedSealed.UnmaskedChild("public")

        val jsonMasked = TestUtil.jsonNativeTestUtil.toMaskedJson(masked)
        val jsonUnmasked = TestUtil.jsonNativeTestUtil.toMaskedJson(unmasked)

        val sutMasked = TestUtil.jsonNativeTestUtil.objectMapper.readTree(jsonMasked)
        val sutUnmasked = TestUtil.jsonNativeTestUtil.objectMapper.readTree(jsonUnmasked)

        softly.assertThat(sutMasked["secret"].asText()).`as` { "Secret" }.isEqualTo("********")
        softly.assertThat(sutUnmasked["value"].asText()).`as` { "Value" }.isEqualTo("public")
    }

    sealed class PartiallyMaskedSealed {
        @Masked
        data class MaskedChild(
            @param:SymbolMask
            val secret: String
        ) : PartiallyMaskedSealed()

        data class UnmaskedChild(
            @param:SymbolMask
            val secret: String
        ) : PartiallyMaskedSealed()
    }

    @Test
    fun `test masking with partially masked sealed class hierarchy`(softly: SoftAssertions) {
        val masked = PartiallyMaskedSealed.MaskedChild("mysecret")
        val unmasked = PartiallyMaskedSealed.UnmaskedChild("mysecret")

        val jsonMasked = TestUtil.jsonNativeTestUtil.toMaskedJson(masked)
        val jsonUnmasked = TestUtil.jsonNativeTestUtil.toMaskedJson(unmasked)

        val sutMasked = TestUtil.jsonNativeTestUtil.objectMapper.readTree(jsonMasked)
        val sutUnmasked = TestUtil.jsonNativeTestUtil.objectMapper.readTree(jsonUnmasked)

        softly.assertThat(sutMasked["secret"].asText()).`as` { "Secret" }.isEqualTo("********")
        softly.assertThat(sutUnmasked["secret"].asText()).`as` { "Secret" }.isEqualTo("mysecret")
    }

    @Masked
    abstract class AbstractWithFullMask {
        @FullMask(mask = "HIDDEN")
        abstract val secret: String
    }

    class AbstractImplWithFullMask(override val secret: String) : AbstractWithFullMask()

    @Test
    fun `test masking with abstract property override`(softly: SoftAssertions) {
        val obj = AbstractImplWithFullMask("top-secret")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(obj)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("HIDDEN")
    }
}
