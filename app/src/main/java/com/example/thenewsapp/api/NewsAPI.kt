package com.example.thenewsapp.api

import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Here we will be creating different requests urls to fetch data
 */

interface NewsAPI {

    // To fetch headlines data we are using "top-headlines" end-point
    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        // different parameters for the url
        @Query("country")
        countryCode : String = "us",
        @Query("page")
        pageNumber : Int = 1,
        @Query("apiKey")
        apiKey : String = API_KEY
    ) : Response<NewsResponse>

    // To fetch search data we are using "everything" end-point
    @GET("v2/everything")
    suspend fun searchForNews(
        // different parameters for the url
        @Query("q")
        searchQuery : String,
        @Query("page")
        pageNumber : Int = 1,
        @Query("apiKey")
        apiKey : String = API_KEY
    ) : Response<NewsResponse>
}