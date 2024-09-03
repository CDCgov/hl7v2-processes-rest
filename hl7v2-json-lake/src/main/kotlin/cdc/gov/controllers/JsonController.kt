package cdc.gov.controllers

import com.google.gson.JsonObject

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import gov.cdc.hl7.bumblebee.HL7JsonTransformer

@Controller("/")  // Base URL for this controller
class JsonController {

    companion object {
        const val PROFILE_FILE_PATH = "PhinGuideProfile_v2.json"
    }

    @Post("/jsonTransformer", consumes = [MediaType.TEXT_PLAIN], produces = [MediaType.APPLICATION_JSON])  // Endpoint URL
    fun redactMessage( @Body content: String,
                      request: HttpRequest<Any>): HttpResponse<Any> {

        var responseContent =""

         try {
             val fullHL7 = buildJson(content)
             if (fullHL7 != null) {
                 responseContent = "{ \"report\": \"${fullHL7}\" }"
             }

        }catch (e: Exception) {
             HttpResponse
                 .badRequest("Error: ${e.message}")
         }

         return HttpResponse.ok(responseContent)

    }


    private fun buildJson(message: String): JsonObject {
        val bumblebee =
            HL7JsonTransformer.getTransformerWithResource(message, PROFILE_FILE_PATH)
        return bumblebee.transformMessage()
    }

}
