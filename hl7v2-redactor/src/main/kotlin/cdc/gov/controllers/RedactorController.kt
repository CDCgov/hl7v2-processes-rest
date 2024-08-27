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
    fun redactMessage(@QueryValue dataStreamId: String?, @Body content: String,
                      request: HttpRequest<Any>): HttpResponse<Any> {
        if (dataStreamId.isNullOrEmpty()) {
            return HttpResponse
                .badRequest("Error: Data Stream ID must be specified as a query parameter 'dataStreamId'.")
        }
        var responseContent =""

         try {
             val configFileName = getConfigFileName(dataStreamId)
             val report = getRedactedReport(content ?: "", configFileName)
             if (report != null) {
                 responseContent = "{ \"report\": \"${report}\" }"
             }

        }catch (e: Exception) {
             HttpResponse
                 .badRequest("Error: ${e.message}")
         }

         return HttpResponse.ok(responseContent)

    }

    private fun getConfigFileName(dataStreamId: String ) : String {
        val fileSuffix = "-config.txt"
        val dataStreamName = dataStreamId.uppercase().trim()
        // return without the '/' in front of the name for use in metadata
        return if (this::class.java.getResource("/profiles/$dataStreamName$fileSuffix") != null) {
            "$dataStreamName$fileSuffix"
        } else {
            // Use default (ELR) config if nothing else exists
            "DEFAULT$fileSuffix"
        }
    }

    fun getRedactedReport(msg: String, configFileName: String): Tuple2<String, List<RedactInfo>>? {
        val dIdentifier = DeIdentifier()
        val configFile = "/profiles/$configFileName"
        val rules = if (this::class.java.getResource(configFile) != null) {
            this::class.java.getResource(configFile)!!.readText().lines()
        } else {
            throw FileNotFoundException("Unable to find redaction config file $configFile")
        }
        return dIdentifier.deIdentifyMessage(msg, rules.toTypedArray())
    }

}
