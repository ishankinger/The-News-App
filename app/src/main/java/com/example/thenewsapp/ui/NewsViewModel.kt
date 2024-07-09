package com.example.thenewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

/**
 * Holds all the data needed for the UI, prepare it for display
 */
class NewsViewModel(app: Application, private val newsRepository : NewsRepository) : AndroidViewModel(app) {

    val headlines : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage : Int = 1
    var headlinesResponse : NewsResponse? = null

    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage : Int = 1
    var searchNewsResponse : NewsResponse? = null
    var newSearchQuery : String? = null
    var oldSearchQuery : String? = null

    init{
        getHeadlines("us")
    }

    /**
     * Here all the database calling function called under viewModelScope
     */
    fun addToFavourites(article : Article)
            = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = newsRepository.getFavouriteNews()

    fun deleteArticle(article: Article)
            = viewModelScope.launch {
        newsRepository.delete(article)
    }

    /**
     * Here all the network calling function under viewModelScope by first checking the internet
     * connectivity and then getting the response and updating current response and it's pages
     */

    fun getHeadlines(countryCode: String) =
        viewModelScope.launch {
            headlinesInternet(countryCode)
        }

    fun searchNews(searchQuery: String) =
        viewModelScope.launch {
            searchNewsInternet(searchQuery)
        }


    // function to update the headlines page and add new articles to the headline response
    private fun handleHeadlinesResponse(response : Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse->
                headlinesPage++
                if(headlinesResponse == null){
                    headlinesResponse = resultResponse
                }
                else{
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headlinesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // function to update the searchNews page and add new article to the search news response
    private fun handleSearchNewsResponse(response : Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse->
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                }
                else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // function to check for the internet connection
    private fun internetConnection(context : Context) : Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply{
            return getNetworkCapabilities(activeNetwork)?.run{
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    // function to get news headlines when there is internet connectivity otherwise give error
    private suspend fun headlinesInternet(countryCode : String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getHeadlines(countryCode,headlinesPage)
                headlines.postValue(handleHeadlinesResponse(response))
            }
            else{
                headlines.postValue(Resource.Error("No internet connection"))
            }
        } catch(t : Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable to connect"))
                else -> headlines.postValue(Resource.Error("No signal"))
            }
        }
    }

    // function to get search news when there is internet connectivity otherwise give error
    private suspend fun searchNewsInternet(searchQuery : String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }
            else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch(t : Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue(Resource.Error("No signal"))
            }
        }
    }
}