package cz.applifting.humansis.model

import com.google.gson.annotations.SerializedName

class GetSaltResponse(@SerializedName("user_id") val userId: Int, val salt: String)