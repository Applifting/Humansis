package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 28, August, 2019
 */
data class Commodity(
    @SerializedName("id") val id: Int,
    @SerializedName("unit") val unit: String,
    @SerializedName("value") val value: Int,
    @SerializedName("description") val description: String?,
    @SerializedName("modality_type") val modalityType: ModalityType
)