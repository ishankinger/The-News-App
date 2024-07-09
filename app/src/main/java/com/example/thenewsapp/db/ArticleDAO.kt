package com.example.thenewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thenewsapp.models.Article

/**
 * Abstract methods for interacting with the database are defined here
  */

@Dao
interface ArticleDAO {

    // if same primary key then old data will be replaced with new data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article : Article) : Long

    // Get the list of all articles
    @Query("SELECT * FROM articles")
    fun getAllArticles() : LiveData<List<Article>>

    // delete the article
    @Delete
    suspend fun deleteArticle(article : Article)
}