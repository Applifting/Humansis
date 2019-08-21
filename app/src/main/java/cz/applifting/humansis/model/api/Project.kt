package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 19, August, 2019
 */
data class Project(
    val id: Int,
    val name: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    val number_of_households: Int? = null,
    val target: Int? = null,
    val notes: String? = null,
    val iso3: String? = null,
    val donors: List<Any>? = null,
    val sectors: List<Any>? = null,
    val archived: Boolean = false,
    val distributions: List<Any>? = null
)