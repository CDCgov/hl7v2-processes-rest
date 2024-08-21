package cdc.gov.model
import com.google.gson.annotations.SerializedName

data class DebatchedItem(
  @SerializedName("item_number")  val itemNumber: Int,
  @SerializedName("item_content")  val itemContent: String
  )
