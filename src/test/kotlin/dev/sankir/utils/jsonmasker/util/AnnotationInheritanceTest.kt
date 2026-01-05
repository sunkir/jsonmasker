package dev.sankir.utils.jsonmasker.util

import dev.sankir.utils.jsonmasker.annotation.FullMask
import dev.sankir.utils.jsonmasker.annotation.Masked
import dev.sankir.utils.jsonmasker.annotation.SymbolMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class AnnotationInheritanceTest {

    @Masked
    open class BaseUser(
        @param:SymbolMask
        open val name: String
    )

    class ChildUser(name: String, val email: String) : BaseUser(name)

    @Masked
    open class BaseUserNoAnnotation(
        open val secret: String
    )

    class ChildUserWithAnnotation(
        @param:FullMask
        override val secret: String
    ) : BaseUserNoAnnotation(secret)

    @Masked
    open class BaseUserWithFullMask(
        @param:FullMask(mask = "BASE_MASK")
        open val secret: String
    )

    class ChildUserInheritedMask(override val secret: String) : BaseUserWithFullMask(secret)

    @Test
    fun `test @Masked annotation inheritance`(softly: SoftAssertions) {
        val user = ChildUser("John", "john@example.com")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("****")
        softly.assertThat(sut["email"].asText()).`as` { "Email" }.isEqualTo("john@example.com")
    }

    @Test
    fun `test masking annotation on overridden property`(softly: SoftAssertions) {
        val user = ChildUserWithAnnotation("my-password")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("[masked]")
    }

    @Test
    fun `test masking annotation inherited from base property`(softly: SoftAssertions) {
        val user = ChildUserInheritedMask("inherited-secret")
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(user)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["secret"].asText()).`as` { "Secret" }.isEqualTo("BASE_MASK")
    }
}
