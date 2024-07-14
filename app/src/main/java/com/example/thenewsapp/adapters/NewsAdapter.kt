package com.example.thenewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thenewsapp.R
import com.example.thenewsapp.models.Article

/**
 * Adapter to show the list of the news with their properties and also navigate by clicking on items
 */

class NewsAdapter(private val listLayout : Int) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private lateinit var articleImage : ImageView
    private lateinit var articleSource : TextView
    private lateinit var articleTitle : TextView
    private lateinit var articleDescription : TextView
    private lateinit var articleDateTime : TextView

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return if(listLayout == 0){
            ArticleViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_news_linear,parent,false)
            )
        } else{
            ArticleViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)
            )
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener : ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        // connecting the local variables to the views of the item news
        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)

        // filling the values of the views of item news
        holder.itemView.apply{

            Glide.with(this)
                .load(article.urlToImage)
                .placeholder(R.drawable.baseline_newspaper_24)
                .into(articleImage)

            article.source?.let{
                articleSource.text = article.source.name
            }

            article.title?.let{
                articleTitle.text = article.title
            }

            article.description?.let{
                articleDescription.text = article.description
            }

            article.publishedAt?.let{
                articleDateTime.text = article.publishedAt
            }

            // setting up the click listener for the items
            setOnClickListener {
                onItemClickListener?.let{
                    it(article)
                }
            }

        }
    }

    // when this called variable onItemClickListener value changes and click listener called
    fun setOnItemClickListener(listener : (Article) -> Unit){
        onItemClickListener = listener
    }
}