package com.example.thenewsapp.db

import androidx.room.TypeConverter
import com.example.thenewsapp.models.Source

/**
 *  Source datatype is not supported by database so we convert it using Type Converters
 */
class Converters {
    @TypeConverter
    fun fromSource(source : Source) : String{
        return source.name
    }

    @TypeConverter
    fun toSource(name : String) : Source{
        return Source(name,name)
    }
}