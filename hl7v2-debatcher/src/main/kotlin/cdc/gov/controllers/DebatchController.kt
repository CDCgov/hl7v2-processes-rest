package cdc.gov.controllers
import cdc.gov.model.DebatchedItem
import cdc.gov.model.DebatchedItemsCollection
import com.google.gson.Gson
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import gov.cdc.hl7.BatchValidator
import com.google.gson.GsonBuilder
import io.micronaut.http.MutableHttpResponse

@Controller("/")
class DebatchController {
    val gson: Gson = GsonBuilder().disableHtmlEscaping().serializeNulls().create()
    @Get(produces = [MediaType.TEXT_PLAIN])
    fun index(): String {
        return "Hello from the Debatcher service. Please post messages to debatch to the /debatch url." // (3)
    }

    @Post(value = "/debatch", consumes = [MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN])
    fun debatchMessages(
        @Body content: String,
        request: HttpRequest<Any>
    ): MutableHttpResponse<String>? {
        return try {
            val messages = BatchValidator(content).debatchMessages()
            var itemNumber = 0
            val messageList = mutableListOf<DebatchedItem>()
            messages.foreach { message ->
//                itemNumber++
                messageList.add(DebatchedItem(++itemNumber, message))
            }
            val collection = DebatchedItemsCollection(totalItems = messageList.size, items = messageList)
            HttpResponse.ok(gson.toJson(collection)).contentEncoding(MediaType.APPLICATION_JSON)
        } catch (e: Exception) {
            HttpResponse.badRequest("An unexpected error occurred: ${e.message}")
        }

    }
}