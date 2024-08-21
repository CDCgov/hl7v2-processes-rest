package cdc.gov.model

import com.google.gson.annotations.SerializedName

data class DebatchedItemsCollection(
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("items") val items: List<DebatchedItem>
)
