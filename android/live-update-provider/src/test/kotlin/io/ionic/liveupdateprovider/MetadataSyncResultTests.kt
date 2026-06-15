package io.ionic.liveupdateprovider

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MetadataSyncResultTests {
    @Test
    fun `default metadata sync result carries metadata`() {
        val result: MetadataSyncResult = DefaultMetadataSyncResult(mapOf("version" to "1.0.0"))

        assertEquals("1.0.0", result.metadata?.get("version"))
    }

    @Test
    fun `default metadata sync result supports missing metadata`() {
        val result: MetadataSyncResult = DefaultMetadataSyncResult()

        assertNull(result.metadata)
    }
}
