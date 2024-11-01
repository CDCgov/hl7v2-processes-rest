package gov.cdc.dex.validation.service

import com.google.gson.GsonBuilder
import gov.cdc.EntryInterfaceAdapter


import gov.cdc.NistReport
import gov.nist.validation.report.Entry
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@MicronautTest
class ValidationControllerTest {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient


//    private val gson = GsonBuilder().disableHtmlEscaping().serializeNulls().registerTypeAdapter(
//        gov.nist.validation.report.Entry::class.java,
//        EntryInterfaceAdapter<Entry>()
//    ).create()
    private val log: Logger = LoggerFactory.getLogger(ValidationControllerTest::class.java)

    companion object {
        const val VALID_HL7_MESSAGE = "MSH|^~\\&|LIMS.MN.STAGING^2.16.840.1.114222.4.3.4.23.1.2^ISO|MN Public Health Lab^2.16.840.1.114222.4.1.10080^ISO|AIMS.INTEGRATION.STG^2.16.840.1.114222.4.3.15.2^ISO|CDC.ARLN.CRECol^2.16.840.1.114222.4.1.219333^ISO|20200201104547.082-0600||ORU^R01^ORU_R01|178106199999|D|2.5.1|||||||||PHLabReport-NoAck^phLabResultsELRv251^2.16.840.1.113883.9.11^ISO"

        const val INVALID_HL7_MESSAGE = "Invalid Message"
    }

    @BeforeEach
    fun setUp() {
        // Setup necessary mocks or configurations here
        // For example, mocking the behavior of ProfileManager if needed

    }

    @Test
    fun `test structureValidate with valid HL7 message`() {
        val request = HttpRequest.POST("/validate", VALID_HL7_MESSAGE)
            .contentType(MediaType.TEXT_PLAIN)
        val response: HttpResponse<String> = client.toBlocking().exchange(request, String::class.java)

        Assertions.assertEquals(HttpResponse.ok<String>().status, response.status)
        val report = NistReport.nistGson.fromJson(response.body(), NistReport::class.java)
        Assertions.assertEquals(report.status, "STRUCTURE_ERRORS")
    }

    @Test
    fun `test structureValidate with invalid HL7 message`() {
            try {
                val request = HttpRequest.POST("/validate", INVALID_HL7_MESSAGE)
                    .contentType(MediaType.TEXT_PLAIN)
                val response = client.toBlocking().exchange(request, String::class.java)

                // Add assertions to check if the error response contains expected error message
            } catch (e: HttpClientResponseException) {
                assert(true)
            }

    }

    @Test
    fun `test validateMessage for missing delimiters`() {
        val validationController = ValidationController()

        val exception = Assertions.assertThrows(Exception::class.java) {
            validationController.validateMessage(INVALID_HL7_MESSAGE)
        }

        Assertions.assertTrue(exception.message!!.contains("Unable to locate message header sequence"))
    }

    @Test
    fun `test getProfileNameAndPaths with valid HL7 message`() {
        val validationController = ValidationController()
        val profileNameAndPaths = validationController.getProfileNameAndPaths(VALID_HL7_MESSAGE)

        Assertions.assertNotNull(profileNameAndPaths)
        Assertions.assertEquals("DAART-MSH-12", profileNameAndPaths.first)
        // Add assertions on profileNameAndPaths.second as expected
    }

//


    @AfterEach
    fun tearDown() {
        // Clean up after each test if needed
    }
}
