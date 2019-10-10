package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 28, August, 2019
 */
data class SelectionCriteria(
    @SerializedName("id") val id: Int,
    @SerializedName("target") val target: String
)