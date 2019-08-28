package cz.applifting.humansis.model.api

import cz.applifting.humansis.model.CommodityType

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 28, August, 2019
 */
data class ModalityType(
    val id: Int,
    val name: CommodityType?
)