package com.meezu.newsapp.db

import androidx.room.TypeConverter
import com.meezu.newsapp.models.Source


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