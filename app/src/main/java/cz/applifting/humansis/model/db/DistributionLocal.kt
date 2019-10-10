package cz.applifting.humansis.model.db

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import cz.applifting.humansis.model.Target

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
@Entity(
    tableName = "distributions",
    foreignKeys = [ForeignKey(
        entity = ProjectLocal::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("projectId"),
        onDelete = CASCADE
    )]
)
data class DistributionLocal(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "numberOfBeneficiaries") val numberOfBeneficiaries: Int,
    @ColumnInfo(name = "commodities") val commodities: List<CommodityLocal>,
    @ColumnInfo(name = "dateOfDistribution") val dateOfDistribution: String,
    @ColumnInfo(name = "projectId") val projectId: Int,
    @ColumnInfo(name = "target") val target: Target,
    @ColumnInfo(name = "completed") val completed: Boolean
)