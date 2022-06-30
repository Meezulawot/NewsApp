package com.meezu.newsapp.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NewsResponse(

    @SerializedName("articles")
    @Expose
    var articles: MutableList<Article>? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("totalResults")
    @Expose
    var totalResults: Int? = null
)
