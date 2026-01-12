package dev.sunkir.utils.jsonmasker.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

internal open class TestUtil {

    companion object {
        val jsonNativeTestUtil = JsonMaskingUtil()
        val externalJsonTestUtil = JsonMaskingUtil(jacksonObjectMapper())
    }
}