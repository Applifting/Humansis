package cz.applifting.humansis.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "provinces" // aka "adm1"
)
data class ProvinceLocal(
    @PrimaryKey val id: Int,
    val name: String
)