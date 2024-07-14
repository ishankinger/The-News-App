package com.example.thenewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.thenewsapp.models.Article

/**
 * Creating the instance of the database
 */
@Database([Article::class], version=1,exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDB : RoomDatabase() {

    abstract fun getArticleDao() : ArticleDAO

    companion object{
        @Volatile
        private var instance : ArticleDB? = null
        private val LOCK = Any()

        operator fun invoke(context : Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context : Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDB::class.java,
                "article_db.db"
            ).build()
    }
}