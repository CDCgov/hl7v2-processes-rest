package cdc.gov.controllers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gov.cdc.hl7.DeIdentifier
import gov.cdc.hl7.RedactInfo
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import scala.Tuple2
import java.io.FileNotFoundException

@Controller("/")  // Base URL for this controller
class RedactorController {

    @Post("/redactor", consumes = [MediaType.TEXT_PLAIN], produces = [MediaType.APPLICATION_JSON])  // Endpoint URL
    fun redactMessage(@Body content: String): HttpResponse<Any> {
        return try {
            // get redacted report
            val report: Pair<String, List<RedactInfo>>? = getRedactedReport(content)

            // unpack report
            var redactedMessage: String = ""
            var redactionReport: List<RedactInfo> = emptyList()
            if (report != null) {
                redactedMessage = report.first
                redactionReport = report.second
            }

            // build JSON array with RedactInfo list
            val redactionReportJsonArray = JsonArray()
            redactionReport.forEach { redactInfo ->
                val redactInfoJson = JsonObject().apply {
                    addProperty("path", redactInfo.path())
                    addProperty("field_index", redactInfo.fieldIndex())
                    addProperty("message", redactInfo.rulemsg())
                    addProperty("rule", redactInfo.condition())
                    addProperty("line_number", redactInfo.lineNumber())
                }
                redactionReportJsonArray.add(redactInfoJson)
            }

            // build response JSON
            val responseJson = JsonObject()
            responseJson.addProperty("redacted_message", redactedMessage)
            responseJson.add("redaction_report", redactionReportJsonArray)

            // issue response
            HttpResponse.ok(responseJson.toString())
        } catch (e: Exception) {
            HttpResponse.badRequest("Error: ${e.message}")
        }
    }

    private fun getRedactedReport(msg: String): Pair<String, List<RedactInfo>>? {
        val dIdentifier = DeIdentifier()
        val configFile = "/profiles/DEFAULT-config.txt"
        val rules = if (this::class.java.getResource(configFile) != null) {
            this::class.java.getResource(configFile)!!.readText().lines()
        } else {
            throw FileNotFoundException("Unable to find redaction config file $configFile")
        }
        val tupleResult: Tuple2<String, List<RedactInfo>>? = dIdentifier.deIdentifyMessage(msg, rules.toTypedArray())
        return tupleResult?.toPair()
    }

    private fun <A, B> Tuple2<A, B>.toPair(): Pair<A, B> {
        return Pair(this._1(), this._2())
    }
}
