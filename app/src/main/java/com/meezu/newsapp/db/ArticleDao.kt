package com.meezu.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.meezu.newsapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(article :Article)

    @Query("Select * From articles")
    fun getNewsArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}