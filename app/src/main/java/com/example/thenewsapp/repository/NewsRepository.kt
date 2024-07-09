package com.example.thenewsapp.repository

import com.example.thenewsapp.api.RetrofitInstance
import com.example.thenewsapp.db.ArticleDatabase
import com.example.thenewsapp.models.Article

/**
 * All the calls for urls and different functions to room database are compiled here
 */

class NewsRepository(private val db : ArticleDatabase) {

    suspend fun getHeadlines(countryCode : String, pageNumber : Int) =
        RetrofitInstance.api.getHeadlines(countryCode,pageNumber)

    suspend fun searchNews(searchQuery : String, pageNumber : Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article : Article) = db.getArticleDao().upsert(article)

    fun getFavouriteNews() = db.getArticleDao().getAllArticles()

    suspend fun delete(article : Article) = db.getArticleDao().deleteArticle(article)
}