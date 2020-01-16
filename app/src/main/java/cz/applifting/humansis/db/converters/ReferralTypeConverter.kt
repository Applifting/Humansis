package cz.applifting.humansis.db.converters

import androidx.room.TypeConverter
import cz.applifting.humansis.model.ReferralType

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
class ReferralTypeConverter {

    @TypeConverter
    fun toEnum(value: String?): ReferralType? {
        return value?.let { ReferralType.valueOf(it) }
    }

    @TypeConverter
    fun toString(value: ReferralType?): String? {
        return value?.toString()
    }
}