package com.meezu.newsapp.repository

import com.meezu.newsapp.api.RetrofitInstance
import com.meezu.newsapp.db.ArticleDatabase
import com.meezu.newsapp.models.Article

class NewsRepository (
    val db : ArticleDatabase
    ){

    suspend fun getTrendingNews(countryCode: String)= RetrofitInstance.getApiInstance()!!.getTrendingNews(countryCode)

    suspend fun searchNews(searchQuery: String) = RetrofitInstance.getApiInstance()!!.getSearchedNews(searchQuery)

    suspend fun insertArticles(article: Article) = db.getArticleDao().saveArticles(article)

    fun getSavedArticles() = db.getArticleDao().getSavedNewsArticles()

    suspend fun deleteSavedArticles(article: Article) = db.getArticleDao().deleteSavedArticle(article)
}