package cz.applifting.humansis.model.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import cz.applifting.humansis.model.ReferralType

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

@Entity(
    tableName = "beneficiaries",
    foreignKeys = [ForeignKey(
        entity = DistributionLocal::class,
        parentColumns = ["id"],
        childColumns = ["distributionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
// each beneficiary (beneficiaryId, ...) can be in multiple distributions
// TODO rename ids (also all "beneficiaryId" variables)
data class BeneficiaryLocal(
    @PrimaryKey val id: Int, // unique id
    val beneficiaryId: Int, // global beneficiary id
    val givenName: String?,
    val familyName: String?,
    val distributionId: Int,
    val distributed: Boolean,
    val vulnerabilities: List<String>,
    val reliefIDs: List<Int>,
    val qrBooklets: List<String>?,
    val edited: Boolean,
    val commodities: List<CommodityLocal>?,
    val nationalId: String?,
    val referralType: ReferralType?,
    val referralNote: String?,
    val isReferralChanged: Boolean = false // special API call needed just for referral
)