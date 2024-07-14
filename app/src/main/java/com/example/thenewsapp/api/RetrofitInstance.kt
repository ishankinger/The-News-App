package com.example.thenewsapp.api

import com.example.thenewsapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Creating retrofit instance which acts as a bridge between url requests and response objects
 */
class RetrofitInstance {

    companion object{

        // instance will only be created when it is first accessed
        private val retrofit by lazy{

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api: NewsAPI by lazy{
            retrofit.create(NewsAPI::class.java)
        }
    }
}