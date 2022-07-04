package com.meezu.newsapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.meezu.newsapp.R
import com.meezu.newsapp.data.models.Article
import com.meezu.newsapp.ui.listener.ClickListener
import com.meezu.newsapp.ui.viewholder.ArticleViewHolder

class NewsAdapter( val listener: ClickListener) : RecyclerView.Adapter<ArticleViewHolder>() {

    private val differCallback = object: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.equals(newItem)
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            DataBindingUtil
                .inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_article,
                    parent,
                    false
            ), listener

        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bindItem(article)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}