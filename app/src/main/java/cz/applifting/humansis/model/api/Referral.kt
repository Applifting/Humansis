package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName
import cz.applifting.humansis.model.ReferralType

data class Referral(
    @SerializedName("type") val type: ReferralType?,
    @SerializedName("comment") val note: String
)