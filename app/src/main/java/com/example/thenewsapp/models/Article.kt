package com.example.thenewsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 *  Data class for object article containing different properties of News
*   As we will be storing articles in Room Database define Entity
*/
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val author : String = "",
    val content : String = "",
    val description : String = "",
    val publishedAt : String = "",
    val source : Source = Source("",""),
    val title : String = "",
    val url : String = "",
    val urlToImage : String = ""
) : Serializable
