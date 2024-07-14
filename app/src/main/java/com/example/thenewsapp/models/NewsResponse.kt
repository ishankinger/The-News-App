package com.example.thenewsapp.models

/**
 * Data class storing the Response Object from the url call
 */
data class NewsResponse(
    val articles : MutableList<Article>,
    val status : String?,
    val totalResults : Int = 0
)