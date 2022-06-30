package com.meezu.newsapp.data.repository

import com.meezu.newsapp.data.network.RetrofitInstance
import com.meezu.newsapp.data.db.ArticleDatabase
import com.meezu.newsapp.data.models.Article

class NewsRepository (val db : ArticleDatabase){

    suspend fun getTrendingNews(countryCode: String, pageNumber: Int)= RetrofitInstance.getApiInstance()!!.getTrendingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) = RetrofitInstance.getApiInstance()!!.getSearchedNews(searchQuery, pageNumber)

    suspend fun insertArticles(article: Article) = db.getArticleDao().saveArticles(article)

    fun getSavedArticles() = db.getArticleDao().getSavedNewsArticles()

    suspend fun deleteSavedArticles(article: Article) = db.getArticleDao().deleteSavedArticle(article)
}