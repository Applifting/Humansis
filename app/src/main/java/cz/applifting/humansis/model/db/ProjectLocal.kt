package cz.applifting.humansis.model.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
@Entity(tableName = "projects")
data class ProjectLocal(
    @PrimaryKey val id: Int,
    val name: String,
    val numberOfHouseholds: Int
) {
    @Ignore
    var isComplete: Boolean = false
}