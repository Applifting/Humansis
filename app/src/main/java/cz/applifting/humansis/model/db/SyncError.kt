package cz.applifting.humansis.model.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 27, November, 2019
 */
@Entity(
    tableName = "errors",
    foreignKeys = [ForeignKey(
        entity = BeneficiaryLocal::class,
        parentColumns = ["id"],
        childColumns = ["beneficiaryId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class SyncError(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val location: String,
    val params: String,
    val code: Int,
    val errorMessage: String,
    val beneficiaryId: Int? = null
)