package com.meezu.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.meezu.newsapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticles(article :Article): Long

    @Query("Select * From articles")
    fun getSavedNewsArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteSavedArticle(article: Article)
}