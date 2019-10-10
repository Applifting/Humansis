package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName
import cz.applifting.humansis.model.CommodityType

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 28, August, 2019
 */
data class ModalityType(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: CommodityType?
)