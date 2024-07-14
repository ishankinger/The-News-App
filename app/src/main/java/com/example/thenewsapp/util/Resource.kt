package com.example.thenewsapp.util

/**
 * Resource class to keep track of the state of the response process of fetching the news
 * Three case possible - Data fetched (Success) , Error in fetching data (Error), Loading
 */
sealed class Resource<T>(
    val data : T? = null,
    val message : String? = null
){
    class Success<T>(data : T) : Resource<T>(data)
    class Error<T>(message : String, data : T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}

