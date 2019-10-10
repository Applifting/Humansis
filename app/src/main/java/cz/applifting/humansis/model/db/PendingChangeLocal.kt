package cz.applifting.humansis.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "pending_changes")
data class PendingChangeLocal(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "date")val date: Date,
    @ColumnInfo(name = "beneficiaryId") val beneficiaryId: Int
)