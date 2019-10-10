package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName
import cz.applifting.humansis.model.Target

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
data class Distribution (
    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String,
    @SerializedName("updated_on") val updatedOn : String?,
    @SerializedName("date_distribution") val dateDistribution : String?,
    @SerializedName("location") val location : Any?,
    @SerializedName("project") val project : Project,
    @SerializedName("selection_criteria") val selection_criteria : List<SelectionCriteria>?,
    @SerializedName("archived") val archived : Boolean,
    @SerializedName("validated") val validated : Boolean,
    @SerializedName("type") val type : Target,
    @SerializedName("commodities") val commodities : List<Commodity>,
    @SerializedName("distribution_beneficiaries") val distributionBeneficiaries : List<Beneficiary>?,
    @SerializedName("completed") val completed : Boolean
)