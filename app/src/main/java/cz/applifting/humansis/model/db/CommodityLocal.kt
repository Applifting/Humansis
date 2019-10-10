package cz.applifting.humansis.model.db

import com.google.gson.annotations.SerializedName

data class CommodityLocal(
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: Int,
    @SerializedName("unit") val unit: String
)