package com.meezu.newsapp.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.meezu.newsapp.data.models.Article
import com.meezu.newsapp.databinding.LayoutItemArticleBinding
import com.meezu.newsapp.ui.listener.ClickListener

class ArticleViewHolder(val binding: LayoutItemArticleBinding, val listener: ClickListener)
    : RecyclerView.ViewHolder(binding.root){

    fun bindItem(article: Article){
        binding.article = article
//        Glide.with(binding.imgArticle.context).load(article.urlToImage).into(binding.imgArticle)
        binding.root.setOnClickListener {
            listener.onclick(article)
        }
    }

}