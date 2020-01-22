package cz.applifting.humansis.model.ui

import cz.applifting.humansis.model.CommodityType
import cz.applifting.humansis.model.Target
import cz.applifting.humansis.model.db.CommodityLocal
import cz.applifting.humansis.model.db.DistributionLocal

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 12. 9. 2019
 */
// TODO remove this and instead use @Ingore in DB entity
@Deprecated("use entity")
data class DistributionModel(
    val id: Int,
    val name: String,
    val numberOfBeneficiaries: Int,
    val commodities: List<CommodityLocal>,
    val dateOfDistribution: String,
    val projectId: Int,
    val target: Target,
    val completed: Boolean,
    val numberOfReachedBeneficiaries: Int
)

@Deprecated("use entity") // ...
val DistributionModel.isQRVoucherDistribution: Boolean
    get() = commodities.any { commodity -> commodity.type == CommodityType.QR_VOUCHER }

// TODO move to DistributionLocal
val DistributionLocal.isQRVoucherDistribution: Boolean
    get() = commodities.any { commodity -> commodity.type == CommodityType.QR_VOUCHER }