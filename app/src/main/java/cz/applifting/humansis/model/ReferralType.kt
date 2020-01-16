package cz.applifting.humansis.model

import com.google.gson.annotations.SerializedName

enum class ReferralType {
    @SerializedName("1") HEALTH,
    @SerializedName("2") PROTECTION,
    @SerializedName("3") SHELTER,
    @SerializedName("4") NUTRITION,
    @SerializedName("5") OTHER
}