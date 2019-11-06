package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 06, November, 2019
 */
data class IdNumber(
    @SerializedName("id_number") val idNumber: String
)