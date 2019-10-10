package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

data class AssingBookletRequest(@SerializedName("code") val code: String)