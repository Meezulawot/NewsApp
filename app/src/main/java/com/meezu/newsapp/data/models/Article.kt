package com.meezu.newsapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "articles"
)
class Article(
    @PrimaryKey
    var url: String,

    @SerializedName("author")
    @Expose
    var author: String? = null,
    @SerializedName("content")
    @Expose
    var content: String? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("publishedAt")
    @Expose
    var publishedAt: String? = null,
    @SerializedName("source")
    @Expose
    var source: Source? = null,
    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("urlToImage")
    @Expose
    var urlToImage: String? = null,


    ) : Serializable