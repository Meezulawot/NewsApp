package com.meezu.newsapp.api

import com.meezu.newsapp.models.NewsResponse
import com.meezu.newsapp.utils.constants.ApiConstants
import com.meezu.newsapp.utils.constants.StringConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET(ApiConstants.TOP_HEADLINES)
    suspend fun getTrendingNews(
        @Query("country")
        countryCode: String,
        @Query(StringConstants.ApiKey)
        apiKey: String = ApiConstants.API_KEY
    ) : Response<NewsResponse>

    @GET(ApiConstants.ALL_NEWS)
    suspend fun getSearchedNews(
        @Query("q")
        searchQuery: String,
        @Query(StringConstants.ApiKey)
        apiKey: String = ApiConstants.API_KEY
    ) : Response<NewsResponse>
}