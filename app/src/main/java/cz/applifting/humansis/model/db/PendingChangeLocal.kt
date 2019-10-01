package cz.applifting.humansis.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "pending_changes")
data class PendingChangeLocal(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val date: Date,
    val beneficiaryId: Int,
    val isQrVoucher: Boolean,
    val distributed: Boolean
)