package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 19, August, 2019
 */
data class Project(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("number_of_households") val numberOfHouseholds: Int?,
    @SerializedName("target") val target: Int?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("iso3") val iso3: String?,
    @SerializedName("donors") val donors: List<Any>?,
    @SerializedName("sectors") val sectors: List<Any>?,
    @SerializedName("archived") val archived: Boolean = false,
    @SerializedName("distributions") val distributions: List<Any>?
)