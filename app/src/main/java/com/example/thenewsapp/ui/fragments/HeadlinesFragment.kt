package com.example.thenewsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thenewsapp.R
import com.example.thenewsapp.adapters.NewsAdapter
import com.example.thenewsapp.databinding.FragmentHeadlinesBinding
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.example.thenewsapp.util.Constants
import com.example.thenewsapp.util.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Fragment showing the headlines news using news Adapter
 */
class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private lateinit var retryButton: Button
    private lateinit var errorText: TextView
    private lateinit var itemHeadlinesError: CardView

    private lateinit var binding: FragmentHeadlinesBinding

    private var listLayout : Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // connecting all the late init variables here
        binding = FragmentHeadlinesBinding.bind(view)

        itemHeadlinesError = view.findViewById(R.id.itemHeadlinesError)
        retryButton = binding.itemHeadlinesError.retryButton
        errorText = binding.itemHeadlinesError.errorText

        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpHeadlinesRecycler()

        // Showing the bottom navigation view
        val bottomNavigationView = (activity as NewsActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE

        // on item click listener of the item news (navigating to the article fragment by passing article)
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply{
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_headlinesFragment_to_articleFragment,bundle)
        }

        // when there will be any change in the data of headlines variable this observer will be called
        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success<*> ->{
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let{newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults/ Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.headlinesPage == totalPages
                        if(isLastPage){
                            binding.recyclerHeadlines.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error<*> ->{
                    hideProgressBar()
                    response.message?.let{message->
                        Toast.makeText(activity,"Error: $message",Toast.LENGTH_SHORT).show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading<*> ->{
                    showProgressBar()
                }
            }
        })

        // on click listener for retryButton
        retryButton.setOnClickListener{
            newsViewModel.getHeadlines("us")
        }
    }

    /**
     * function to show or hide Progress Bar and Error message
     */
    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage(){
        itemHeadlinesError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message : String){
        itemHeadlinesError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    /**
     * scroll Listener to apply pagination on the headlines response
     */
    private val scrollListener = object : RecyclerView.OnScrollListener(){

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate = isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                // via this call headlines value in notesViewModel will change and observer will be called
                // which submit the list of headlines responses to adapter to be visible on the screen
                newsViewModel.getHeadlines("us")
                isScrolling = false
            }
        }
    }

    // function to connect the recycler view with our news adapter
    private fun setUpHeadlinesRecycler(){
        newsAdapter = NewsAdapter(listLayout)
        binding.recyclerHeadlines.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlinesFragment.scrollListener)
        }
    }

}