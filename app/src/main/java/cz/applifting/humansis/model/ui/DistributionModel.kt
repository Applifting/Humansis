package cz.applifting.humansis.model.ui

import cz.applifting.humansis.model.Target

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 12. 9. 2019
 */

data class DistributionModel(
    val id: Int,
    val name: String,
    val numberOfBeneficiaries: Int,
    val commodities: List<String>,
    val dateOfDistribution: String,
    val projectId: Int,
    val target: Target,
    val completed: Boolean,
    val numberOfReachedBeneficiaries: Int
)