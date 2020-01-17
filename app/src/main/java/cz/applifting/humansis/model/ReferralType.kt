package cz.applifting.humansis.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import cz.applifting.humansis.R

enum class ReferralType(
    @StringRes val textId: Int
) {
    @SerializedName("1") HEALTH(R.string.referral_type_health),
    @SerializedName("2") PROTECTION(R.string.referral_type_protection),
    @SerializedName("3") SHELTER(R.string.referral_type_shelter),
    @SerializedName("4") NUTRITION(R.string.referral_type_nutrition),
    @SerializedName("5") OTHER(R.string.referral_type_other);
}