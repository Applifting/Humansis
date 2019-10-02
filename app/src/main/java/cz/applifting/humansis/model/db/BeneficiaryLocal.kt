package cz.applifting.humansis.model.db

import androidx.room.Entity
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
    val reliefs: List<Int>,
    val booklets: List<String>
)