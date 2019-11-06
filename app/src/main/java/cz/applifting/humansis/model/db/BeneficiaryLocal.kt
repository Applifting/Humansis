package cz.applifting.humansis.model.db

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
    val beneficiaryId: Int,
    val givenName: String?,
    val familyName: String?,
    val distributionId: Int,
    val distributed: Boolean,
    val vulnerabilities: List<String>,
    val reliefIDs: List<Int>,
    val qrBooklets: List<String>?,
    val edited: Boolean,
    val commodities: List<CommodityLocal>?,
    val nationalId: String?
) {
    // Used in UI only
    @Ignore
    var currentViewing: Boolean = false
}