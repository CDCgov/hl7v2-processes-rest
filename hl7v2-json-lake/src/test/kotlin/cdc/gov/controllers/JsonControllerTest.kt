package cdc.gov.controllers

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.micronaut.http.HttpResponse
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
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
        // arrange
        val inputMessage = loadTestResource("COVID19_ELR_01.hl7")

        // act
        val response: HttpResponse<Any> = jsonController.transformMessage(inputMessage)

        // ensure response is successful
        assertTrue(response.status.code in 200..299, "Expected successful response")

        val responseStr: String = response.body().toString()
        val gson = GsonBuilder().create()
        val responseJson: JsonObject = gson.fromJson(responseStr, JsonObject::class.java)

        // assert that the response contains the expected HL7 segments
        assertTrue(responseJson.has("MSH"), "Response should contain MSH segment")
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
