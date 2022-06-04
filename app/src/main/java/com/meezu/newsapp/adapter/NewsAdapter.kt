package com.meezu.newsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.meezu.newsapp.databinding.LayoutItemArticleBinding
import com.meezu.newsapp.models.Article

class NewsAdapter(
    var context: Context,
    val listener : ClickListener
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(
        val binding: LayoutItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root){
            fun bindItem(article: Article){
                binding.tvTitle.text = article.title
                binding.tvSource.text = article.source.name
                binding.tvPublishedAt.text = article.publishedAt
                Glide.with(context).load(article.urlToImage).into(binding.imgArticle)

//                binding.root.setOnClickListener {
//                    listener.onclick(article)
//                }
            }
        }

    private val differCallback = object: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bindItem(article)
        holder.binding.root.setOnClickListener {
            listener.onclick(article)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface ClickListener{
        fun onclick(article: Article)
    }

}