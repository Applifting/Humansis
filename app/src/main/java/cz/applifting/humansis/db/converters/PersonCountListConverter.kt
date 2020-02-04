package cz.applifting.humansis.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cz.applifting.humansis.model.PersonCount


class PersonCountListConverter {
    @TypeConverter
    fun toList(value: String): List<PersonCount> {
        val listType = object : TypeToken<List<PersonCount>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<PersonCount>): String {
        return Gson().toJson(list)
    }
}