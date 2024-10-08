package cdc.gov.controllers

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.http.HttpResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import jakarta.inject.Inject
import io.micronaut.http.MediaType
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@MicronautTest
class RedactorControllerTest {

    @Inject
    lateinit var redactorController: RedactorController

    @Test
    fun `test redactMessage returns successful JSON response`() {
        // arrange
        val inputMessage = loadTestResource("COVID19_ELR_01.hl7")

        // act
        val response: HttpResponse<Any> = redactorController.redactMessage(inputMessage)

        // assert
        assertEquals(200, response.status.code, "Expected HTTP 200 status")

        // check that the content type is JSON
        assertEquals(MediaType.APPLICATION_JSON, response.contentType.orElse(null), "Expected application/json content type")

        // convert the response body to a string and parse as JSON
        val responseBody = response.body.toString()
        val json = com.google.gson.JsonParser.parseString(responseBody).asJsonObject

        // check the redacted_message field
        assertTrue(json.has("redacted_message"), "Response should contain 'redacted_message'")
        val redactedMessage = json.get("redacted_message").asString
        assertNotNull(redactedMessage, "'redacted_message' should not be null")

        // check the redaction_report field
        assertTrue(json.has("redaction_report"), "Response should contain 'redaction_report'")
        val redactionReport = json.get("redaction_report").asJsonArray
        assertNotNull(redactionReport, "'redaction_report' should not be null")
        assertTrue(redactionReport.size() > 0, "'redaction_report' should contain redactions")
    }

    private fun loadTestResource(fileName: String): String {
        return try {
            val filePath = Paths.get("src/test/resources", fileName)
            Files.readString(filePath)
        } catch (e: IOException) {
            throw RuntimeException("Failed to load test resource: $fileName", e)
        }
    }
}