package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

data class Booklet(@SerializedName("id") val id: Int,
                   @SerializedName("code") val code: String,
                   @SerializedName("currency") val currency: String,
                   @SerializedName("status") val status: Int,
                   @SerializedName("vouchers") val vouchers: List<Voucher>)