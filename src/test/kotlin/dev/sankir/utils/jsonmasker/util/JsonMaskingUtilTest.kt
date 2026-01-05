package dev.sankir.utils.jsonmasker.util

import dev.sankir.utils.jsonmasker.annotation.FullMask
import dev.sankir.utils.jsonmasker.annotation.Masked
import dev.sankir.utils.jsonmasker.annotation.SymbolMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(SoftAssertionsExtension::class)
class JsonMaskingUtilTest {

    private val util = TestUtil.jsonNativeTestUtil

    @Test
    fun `test maskJsonString with key config`(softly: SoftAssertions) {
        val json = """{"password": "123", "user_id": "ABC-123", "other": "val"}"""
        val config = MaskingConfiguration().apply {
            addKeyConfig("password", KeyMaskingConfig(strategyClass = FullMask::class, mask = "***"))
            addKeyConfig("*_id", KeyMaskingConfig(strategyClass = SymbolMask::class))
        }

        val jsonResult = util.maskJsonString(json, config)
        val sut = util.objectMapper.readTree(jsonResult)

        softly.assertThat(sut["password"].asText()).`as` { "Password" }.isEqualTo("***")
        softly.assertThat(sut["user_id"].asText()).`as` { "User_id" }.isEqualTo("*******")
        softly.assertThat(sut["other"].asText()).`as` { "Other" }.isEqualTo("val")
    }

    @Test
    fun `test maskJsonString with wildcard and nested`(softly: SoftAssertions) {
        val json = """
            {
                "data": {
                    "account_number": "123456",
                    "card_number": "987654"
                },
                "status": "active"
            }
        """.trimIndent()

        val config = MaskingConfiguration().apply {
            addKeyConfig("*_number", KeyMaskingConfig(strategyClass = SymbolMask::class))
        }

        val jsonResult = util.maskJsonString(json, config)
        val sut = util.objectMapper.readTree(jsonResult)

        softly.assertThat(sut["data"]["account_number"].asText()).`as` { "AccountNumber" }.isEqualTo("******")
        softly.assertThat(sut["data"]["card_number"].asText()).`as` { "CardNumber" }.isEqualTo("******")
        softly.assertThat(sut["status"].asText()).`as` { "Status" }.isEqualTo("active")
    }

    @Test
    fun `test maskJsonString with invalid json`(softly: SoftAssertions) {
        val json = """{"key": "value""""
        val config = MaskingConfiguration().apply {
            addKeyConfig("key", KeyMaskingConfig(strategyClass = FullMask::class))
        }

        softly.assertThatThrownBy { util.maskJsonString(json, config) }
            .isInstanceOf(Exception::class.java)
    }

    @Test
    fun `test maskJsonString with missing keys`(softly: SoftAssertions) {
        val json = """{"name": "John"}"""
        val config = MaskingConfiguration().apply {
            addKeyConfig("password", KeyMaskingConfig(strategyClass = FullMask::class))
        }

        val jsonResult = util.maskJsonString(json, config)
        val sut = util.objectMapper.readTree(jsonResult)

        softly.assertThat(sut["name"].asText()).`as` { "Name" }.isEqualTo("John")
    }

    @Test
    fun `test maskJsonString with null values`(softly: SoftAssertions) {
        val json = """{"password": null}"""
        val config = MaskingConfiguration().apply {
            addKeyConfig("password", KeyMaskingConfig(strategyClass = FullMask::class))
        }

        val jsonResult = util.maskJsonString(json, config)
        val sut = util.objectMapper.readTree(jsonResult)

        softly.assertThat(sut["password"].isNull).`as` { "Password" }.isTrue()
    }

    @Test
    fun `test maskJsonString with empty config`(softly: SoftAssertions) {
        val json = """{"password": "123"}"""
        val config = MaskingConfiguration()

        val jsonResult = util.maskJsonString(json, config)
        val sut = util.objectMapper.readTree(jsonResult)

        softly.assertThat(sut["password"].asText()).`as` { "Password" }.isEqualTo("123")
    }

    @Test
    fun `test maskJsonString with deep nesting`(softly: SoftAssertions) {
        val json = """{"level1": {"level2": {"level3": "secret"}}}"""
        val config = MaskingConfiguration().apply {
            addKeyConfig("level3", KeyMaskingConfig(strategyClass = SymbolMask::class))
        }

        val jsonResult = util.maskJsonString(json, config)
        val sut = util.objectMapper.readTree(jsonResult)

        softly.assertThat(sut["level1"]["level2"]["level3"].asText()).`as` { "Level3" }.isEqualTo("******")
    }

    @Test
    fun `test maskJsonString with array of objects`(softly: SoftAssertions) {
        val json = """[{"id": 1, "secret": "abc"}, {"id": 2, "secret": "def"}]"""
        val config = MaskingConfiguration().apply {
            addKeyConfig("secret", KeyMaskingConfig(strategyClass = SymbolMask::class))
        }

        val jsonResult = util.maskJsonString(json, config)
        val sut = util.objectMapper.readTree(jsonResult)

        softly.assertThat(sut[0]["secret"].asText()).`as` { "FirstSecret" }.isEqualTo("***")
        softly.assertThat(sut[1]["secret"].asText()).`as` { "SecondSecret" }.isEqualTo("***")
    }

    @Test
    fun `test maskJsonString disabling masking`(softly: SoftAssertions) {
        val json = """{"password":"123"}"""
        val config = MaskingConfiguration().apply {
            addKeyConfig("password", KeyMaskingConfig(strategyClass = FullMask::class))
        }

        val jsonResult = util.maskJsonString(json, config, mask = false)
        val sut = util.objectMapper.readTree(jsonResult)

        softly.assertThat(sut["password"].asText()).`as` { "Password" }.isEqualTo("123")
    }

    @Test
    fun `test toJson does not mask fields`(softly: SoftAssertions) {
        @Masked
        data class SensitiveUser(
            @param:FullMask
            val password: String,
            @param:SymbolMask
            val email: String
        )
        val user = SensitiveUser(password = "secret", email = "test@example.com")

        val json = util.toJson(user)
        val sut = util.objectMapper.readTree(json)

        softly.assertThat(sut["password"].asText()).isEqualTo("secret")
        softly.assertThat(sut["email"].asText()).isEqualTo("test@example.com")
    }

    @Test
    fun `test sequential masking with same util instance`(softly: SoftAssertions) {
        @Masked
        data class TestUser(
            @param:FullMask
            val password: String
        )
        val user = TestUser(password = "secret123")

        val unmaskedJson = util.toJson(user)
        val unmaskedNode = util.objectMapper.readTree(unmaskedJson)
        softly.assertThat(unmaskedNode["password"].asText()).isEqualTo("secret123")

        val maskedJson = util.toMaskedJson(user)
        val sut = util.objectMapper.readTree(maskedJson)
        softly.assertThat(sut["password"].asText()).isEqualTo("[masked]")
    }
}
