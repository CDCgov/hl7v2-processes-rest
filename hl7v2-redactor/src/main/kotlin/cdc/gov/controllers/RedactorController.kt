package cdc.gov.controllers

import gov.cdc.hl7.DeIdentifier
import gov.cdc.hl7.RedactInfo
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import scala.Tuple2
import java.io.FileNotFoundException

@Controller("/")  // Base URL for this controller
class RedactorController {

    @Post("/redactor", consumes = [MediaType.TEXT_PLAIN], produces = [MediaType.APPLICATION_JSON])  // Endpoint URL
    fun redactMessage( @Body content: String,
                      request: HttpRequest<Any>): HttpResponse<Any> {

        var responseContent =""

         try {
             val report = getRedactedReport(content ?: "")
             if (report != null) {
                 responseContent = "{ \"report\": \"${report}\" }"
             }

        }catch (e: Exception) {
             HttpResponse
                 .badRequest("Error: ${e.message}")
         }

         return HttpResponse.ok(responseContent)

    }

    
    fun getRedactedReport(msg: String): Tuple2<String, List<RedactInfo>>? {
        val dIdentifier = DeIdentifier()
        val configFile = "/profiles/DEFAULT-config.txt"
        val rules = if (this::class.java.getResource(configFile) != null) {
            this::class.java.getResource(configFile)!!.readText().lines()
        } else {
            throw FileNotFoundException("Unable to find redaction config file $configFile")
        }
        return dIdentifier.deIdentifyMessage(msg, rules.toTypedArray())
    }

}
