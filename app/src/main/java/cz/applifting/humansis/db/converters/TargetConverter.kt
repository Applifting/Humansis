package cz.applifting.humansis.db.converters

import androidx.room.TypeConverter
import cz.applifting.humansis.model.Target

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
class TargetConverter {

    @TypeConverter
    fun toEnum(value: String): Target {
        return Target.valueOf(value)
    }

    @TypeConverter
    fun toString(value: Target): String {
        return value.toString()
    }
}