package cdc.gov.controllers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import gov.cdc.HL7JsonTransformer

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post


@Controller("/")  // Base URL for this controller
class JsonController {

    companion object {
        const val PROFILE_FILE_PATH = "PhinGuideProfile_v2.json"
        val gsonNoNulls: Gson = GsonBuilder().create()
    }

    @Post("/jsonTransformer", consumes = [MediaType.TEXT_PLAIN], produces = [MediaType.APPLICATION_JSON])  // Endpoint URL
    fun transformMessage(@Body content: String): HttpResponse<Any> {

        var responseContent =""

         try {
             val fullHL7WithNulls = buildJson(content)
             val fullHL7 = gsonNoNulls.toJsonTree(fullHL7WithNulls).asJsonObject
             responseContent = "{${fullHL7}}"

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
