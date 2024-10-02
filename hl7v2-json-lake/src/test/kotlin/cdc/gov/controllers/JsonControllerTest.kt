package cdc.gov.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


@MicronautTest
class JsonControllerTest {

    @Inject
    lateinit var jsonController: JsonController

    @Test
    fun `test transformMessage with valid HL7 message`(): Unit {
        // Arrange
        val inputMessage = loadTestResource("COVID19_ELR_01.hl7")

        // Act
        val response: HttpResponse<Any> = jsonController.transformMessage(inputMessage)

        // Ensure response is successful
        assertTrue(response.status.code in 200..299, "Expected successful response")

        // Extract the response body
        val responseHl7: String = response.body().toString()

        // Assert
        assertTrue(responseHl7.contains("PID"), "Response should contain PID segment")
        assertTrue(responseHl7.contains("OBR"), "Response should contain OBR segment")
        assertTrue(responseHl7.startsWith("MSH"), "Response should start with MSH segment")
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
