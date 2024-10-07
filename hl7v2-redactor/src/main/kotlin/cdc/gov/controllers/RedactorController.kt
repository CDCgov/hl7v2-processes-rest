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
            // Call to the redaction logic
            val report: String = getRedactedReport(content).toString()

            // Step 1: Remove surrounding parentheses
            val trimmedResponse = report.removeSurrounding("(", ")")
            val lastCommaIndex = trimmedResponse.lastIndexOf(",[")
            var redactedMsg = ""
            var cleanedRedactList: List<String> = emptyList()

            if (lastCommaIndex != -1) {
                // Extract the redacted message and the redaction report
                redactedMsg = trimmedResponse.substring(0, lastCommaIndex).trim()
                val redactInfoList = trimmedResponse.substring(lastCommaIndex + 1).trim()

                // Step 2: Clean up the list by removing brackets and splitting it into individual entries
                cleanedRedactList = redactInfoList
                    .removePrefix("[")
                    .removeSuffix("]")
                    .split("),")
                    .map { it.trim().plus(")") }  // Ensure each entry ends with ')'
            }

            // Step 3: Build the JSON response
            val reportJson = JsonObject()
            reportJson.addProperty("redacted_message", redactedMsg)

            // Adding the list of redactions as a JSON array
            val redactionsArray = JsonArray()
            cleanedRedactList.forEach { redactionsArray.add(it) }
            reportJson.add("redaction_report", redactionsArray)

            // Return the JSON response
            HttpResponse.ok(reportJson.toString())
        } catch (e: Exception) {
            HttpResponse.badRequest("Error: ${e.message}")
        }
    }

    private fun getRedactedReport(msg: String): Tuple2<String, List<RedactInfo>>? {
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
