package dev.sankir.utils.jsonmasker.util

import dev.sankir.utils.jsonmasker.annotation.Masked
import dev.sankir.utils.jsonmasker.annotation.SymbolMask
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
internal class CollectionMaskingTest {

    @Masked
    data class User(
        @param:SymbolMask
        val name: String
    )

    @Masked
    data class Team(
        val members: List<User>
    )

    @Masked
    data class ArrayTeam(
        val members: Array<User>
    )

    @Masked
    data class Registry(
        val users: Map<String, User>
    )

    @Test
    fun `test masking inside List`(softly: SoftAssertions) {
        val team = Team(listOf(User("John"), User("Alice")))
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(team)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["members"][0]["name"].asText()).`as` { "Member 0 Name" }.isEqualTo("****")
        softly.assertThat(sut["members"][1]["name"].asText()).`as` { "Member 1 Name" }.isEqualTo("*****")
    }

    @Test
    fun `test masking inside Array`(softly: SoftAssertions) {
        val team = ArrayTeam(arrayOf(User("Bob")))
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(team)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["members"][0]["name"].asText()).`as` { "Member 0 Name" }.isEqualTo("***")
    }

    @Test
    fun `test masking inside Map values`(softly: SoftAssertions) {
        val registry = Registry(mapOf("primary" to User("Charlie")))
        val json = TestUtil.jsonNativeTestUtil.toMaskedJson(registry)
        val sut = TestUtil.jsonNativeTestUtil.objectMapper.readTree(json)

        softly.assertThat(sut["users"]["primary"]["name"].asText()).`as` { "Primary User Name" }.isEqualTo("*******")
    }
}
