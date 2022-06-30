package com.meezu.newsapp.ui.listener

import com.meezu.newsapp.data.models.Article

interface ClickListener{
    fun onclick(article: Article)
}