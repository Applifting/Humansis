package cz.applifting.humansis.model.ui

import cz.applifting.humansis.model.db.DistributionLocal

data class DistributionItemWrapper(
    val distribution: DistributionLocal,
    val numberOfReachedBeneficiaries: Int
)