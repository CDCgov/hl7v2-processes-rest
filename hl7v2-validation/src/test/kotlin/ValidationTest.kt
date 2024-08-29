import gov.cdc.dex.validation.service.ValidationController
import org.junit.jupiter.api.Test

class ValidationTest {

    @Test
    fun testExtractDAARTSpec() {
        val fn = ValidationController()
        val msh = "MSH|^~\\&|LIMS.MN.STAGING^2.16.840.1.114222.4.3.4.23.1.2^ISO|MN Public Health Lab^2.16.840.1.114222.4.1.10080^ISO|AIMS.INTEGRATION.STG^2.16.840.1.114222.4.3.15.2^ISO|CDC.ARLN.CRECol^2.16.840.1.114222.4.1.219333^ISO|20200201104547.082-0600||ORU^R01^ORU_R01|178106199999|D|2.5.1|||||||||PHLabReport-NoAck^phLabResultsELRv251^2.16.840.1.113883.9.11^ISO"
        val profile = fn.getProfileNameAndPaths(msh, "DAART").first
        println(profile)
        println("==============================")
    }
}