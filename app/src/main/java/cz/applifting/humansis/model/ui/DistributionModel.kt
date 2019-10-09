package cz.applifting.humansis.model.ui

import cz.applifting.humansis.model.Target
import cz.applifting.humansis.model.db.CommodityLocal

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 12. 9. 2019
 */
// TODO remove this and instead use @Ingore in DB entity
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