package com.meezu.newsapp.data.db

import androidx.room.TypeConverter
import com.meezu.newsapp.data.models.Source


class Converters {

    @TypeConverter
    fun fromSourceToString(source: Source): String?{
        return source.name
    }

    @TypeConverter
    fun fromStringToSource(name: String?): Source {
        return Source(name!!, name)
    }
}