package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

data class DistributionBeneficiary(
    @SerializedName("id") val id: Int,
    @SerializedName("beneficiary") val beneficiary: Beneficiary
)