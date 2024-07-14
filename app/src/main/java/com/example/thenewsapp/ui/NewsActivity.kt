package com.example.thenewsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.ActivityNewsBinding
import com.example.thenewsapp.db.ArticleDB
import com.example.thenewsapp.repository.NewsRepository

/**
 * Activity containing all the fragments of the application and Here we are also instantiating notes
 * View Model so that all the function of notes View Model can be used in all the fragments of this activity
 */

class NewsActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding : ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // defining notesViewModel and passing it with parameters that are newsRepo and application
        val newsRepository = NewsRepository(ArticleDB(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        newsViewModel = ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

        // connecting Nav Controller to bottom navigation view
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

    }
}