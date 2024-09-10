package gov.cdc.dex.validation.service



import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import gov.cdc.ProfileManager

import gov.cdc.hl7.HL7StaticParser
import gov.cdc.NistReport
import gov.cdc.ResourceFileFetcher
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import org.slf4j.LoggerFactory
import java.util.*




data class ProfileIdentifier(
    @SerializedName("data_stream_id") val dataStreamId : String,
    @SerializedName("identifier_paths") val identifierPaths: List<String>
)

@Controller("/")
class ValidationController() {

    companion object {
        private val nistValidators = mutableMapOf<String, ProfileManager?>()
        private val log = LoggerFactory.getLogger(ValidationController::class.java.name)
        private const val DEFAULT_SPEC_PROFILE = "MSH-12"
        private const val HL7_MSH = "MSH|"
        private const val HL7_SUBDELIMITERS = "^~\\&"
        const val PROFILE_CONFIG_FILE_PATH = "profiles/profile_config.json"
        val profileConfigJson = this::class.java.getResource("/$PROFILE_CONFIG_FILE_PATH")?.readText()
        val gson = GsonBuilder().disableHtmlEscaping().serializeNulls().create()
        val profileConfig = gson.fromJson(profileConfigJson, ProfileIdentifier::class.java)

    }


    @Post("/validator", consumes = [MediaType.TEXT_PLAIN], produces = [MediaType.APPLICATION_JSON])
    fun structureValidate(
        @Body content: String
    ): HttpResponse<String> {
        log.info("AUDIT::Executing Validation of message...")

        // Assuming 'content' is never null and is required
        val resultData = validateMessage(content)

        log.info("Message successfully redacted and validated")
        return HttpResponse.ok(gson.toJson(resultData))

    }


    private fun validateMessage(hl7Message: String): NistReport {
        val profileNameAndPaths = getProfileNameAndPaths(hl7Message)
        return getStructureReport(hl7Message, profileNameAndPaths)
    }

    fun getProfileNameAndPaths(hl7Content: String): Pair<String, List<String>> {
        validateHL7Delimiters(hl7Content)
        val dataStreamName = "DAART"
        val profileList = profileConfig.identifierPaths

        // if the route is not specified in the config file, assume the default of MSH-12
        val profileIdPaths = profileList.ifEmpty { listOf(DEFAULT_SPEC_PROFILE) }

        val prefix = "$dataStreamName-"
        val profileName = try {
            prefix + profileIdPaths.map { path ->
                HL7StaticParser.getFirstValue(hl7Content, path).get().uppercase()
            }.reduce { acc, map -> "$acc-$map" }
        } catch (e: NoSuchElementException) {
            throw Exception(
                "Unable to load validation profile: " +
                        "One or more values in the profile path(s)" +
                        " ${profileIdPaths.joinToString()} are missing."
            )
        }
        return Pair(profileName, profileIdPaths)

    }

    private fun validateHL7Delimiters(hl7Message: String) {
        val msg = hl7Message.trim()
        val mshPos = msg.indexOf(HL7_MSH)
        if (mshPos > -1) {
            val delimiters =
                msg.substring(mshPos + HL7_MSH.length, mshPos + (HL7_MSH.length + HL7_SUBDELIMITERS.length))
            if (delimiters != HL7_SUBDELIMITERS) {
                throw Exception("Invalid delimiters found in message header: found '$delimiters', expected '$HL7_SUBDELIMITERS'")
            }
        } else {
            throw Exception("Unable to locate message header sequence '$HL7_MSH'")
        }
    }


    private fun getStructureReport(
        hl7Message: String,
        profileInfo: Pair<String, List<String>>
    ): NistReport {
        val profileName = profileInfo.first
        val profilePaths = profileInfo.second
        val nistValidator = getNistValidator(profileName)
        if (nistValidator != null) {
            val report = nistValidator.validate(hl7Message)
            report.status = if (!report.status.isNullOrEmpty() && ("ERROR" !in report.status + "")) {
                report.status + ""
            } else if ("ERROR" in report.status + "") {
                "STRUCTURE_ERRORS"
            } else {
                "UNKNOWN"
            }
            return report
        } else {
            throw Exception(
                "Unable to find validation profile named $profileName."
                        + " Either the data stream ID DAART or the data in HL7 path(s) " +
                        "'${profilePaths.joinToString()}' is invalid."
            )
        }
    }

    fun getNistValidator(profileName: String) : ProfileManager? {
        if (nistValidators[profileName] == null) {
            loadNistValidator(profileName)
        }
        return nistValidators[profileName]
    }

    private fun loadNistValidator(profileName : String)  {
        val validator = try {
            ProfileManager(ResourceFileFetcher(), "/profiles/$profileName")
        } catch (e : Exception) {
            log.error("${e.message}")
            null
        }
        nistValidators[profileName] = validator
    }

}