package com.example.thenewsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thenewsapp.repository.NewsRepository

/**
 * this class instantiate the NewsViewModel
 */
class NewsViewModelProviderFactory(val app : Application, private val newsRepository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T{
        return NewsViewModel(app,newsRepository) as T
    }
}