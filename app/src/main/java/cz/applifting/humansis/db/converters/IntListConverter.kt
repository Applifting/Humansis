package cz.applifting.humansis.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 29, September, 2019
 */
class IntListConverter {
    @TypeConverter
    fun toList(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<Int>): String {
        return Gson().toJson(list)
    }
}