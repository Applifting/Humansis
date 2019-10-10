package cz.applifting.humansis.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

@Entity(tableName = "beneficiaries")
data class BeneficiaryLocal(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "beneficiaryId") val beneficiaryId: Int,
    @ColumnInfo(name = "givenName") val givenName: String?,
    @ColumnInfo(name = "familyName") val familyName: String?,
    @ColumnInfo(name = "distributionId") val distributionId: Int,
    @ColumnInfo(name = "distributed") val distributed: Boolean,
    @ColumnInfo(name = "vulnerabilities") val vulnerabilities: List<String>,
    @ColumnInfo(name = "reliefIDs") val reliefIDs: List<Int>,
    @ColumnInfo(name = "qrBooklets") val qrBooklets: List<String>?,
    @ColumnInfo(name = "edited") val edited: Boolean,
    @ColumnInfo(name = "commodities") val commodities: List<CommodityLocal>?
) {
    // Used in UI only
    @Ignore
    var currentViewing: Boolean = false
}