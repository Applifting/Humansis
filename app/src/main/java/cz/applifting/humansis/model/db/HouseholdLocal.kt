package cz.applifting.humansis.model.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import cz.applifting.humansis.model.PersonCount

@Entity(
    tableName = "households",
    foreignKeys = [ForeignKey(
        entity = BeneficiaryLocal::class,
        parentColumns = ["id"],
        childColumns = ["householdHeadId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ProvinceLocal::class,
        parentColumns = ["id"],
        childColumns = ["provinceId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class HouseholdLocal(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val householdHeadId: Int,
    val provinceId: Int,
    val addressNumber: String,
    val addressStreet: String,
    val addressPostcode: String,
    val note: String,
    val members: List<PersonCount>
)