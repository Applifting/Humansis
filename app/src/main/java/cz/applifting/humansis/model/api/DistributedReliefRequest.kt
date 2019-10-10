package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

data class DistributedReliefRequest(@SerializedName("ids") val ids: List<Int>)