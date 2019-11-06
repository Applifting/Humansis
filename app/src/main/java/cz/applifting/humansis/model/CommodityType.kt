package cz.applifting.humansis.model

import com.google.gson.annotations.SerializedName
import cz.applifting.humansis.R

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 28, August, 2019
 */
enum class CommodityType(val drawableResId: Int) {
    @SerializedName("Cash") CASH(R.drawable.ic_commodity_cash),
    @SerializedName("Loan") LOAN(R.drawable.ic_commodity_loan),
    @SerializedName("RTE Kit") RTE_KIT(R.drawable.ic_commodity_rte_kit),
    @SerializedName("Paper Voucher") PAPER_VOUCHER(R.drawable.ic_commodity_paper_voucher),
    @SerializedName("Food") FOOD(R.drawable.ic_commodity_food),
    @SerializedName("QR Code Voucher") QR_VOUCHER(R.drawable.ic_commodity_voucher),
    @SerializedName("Mobile Money") MOBILE_MONEY(0)
}