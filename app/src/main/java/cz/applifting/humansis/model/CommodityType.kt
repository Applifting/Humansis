package cz.applifting.humansis.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 28, August, 2019
 */
enum class CommodityType {
    @SerializedName("Cash") CASH,
    @SerializedName("Loan") LOAN,
    @SerializedName("RTE Kit") RTE_KIT,
    @SerializedName("Paper Voucher") PAPER_VOUCHER,
    @SerializedName("Food") FOOD
}