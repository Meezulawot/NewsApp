package com.meezu.newsapp.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meezu.newsapp.data.models.Article
import com.meezu.newsapp.data.models.NewsResponse
import com.meezu.newsapp.data.repository.NewsRepository
import com.meezu.newsapp.utils.NewsApplication
import com.meezu.newsapp.utils.Resource
import com.meezu.newsapp.utils.constants.StringConstants
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    application: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(application) {

    val TAG = NewsViewModel::class.java.simpleName
    val news: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var pageNumber = 1
    var newsResponse: NewsResponse? = null

    init {
        getTrendingNews(StringConstants.country_code)
    }

    fun getTrendingNews(country: String) = viewModelScope.launch {
        news.postValue(Resource.Loading())
        try {
            if (checkInternetConnection()) {
                val response = newsRepository.getTrendingNews(country, pageNumber)
                    news.postValue(handleNewsResponse(response))
            } else {
                news.postValue(Resource.Error(StringConstants.network_error))
            }
        } catch (e: Throwable) {
            when (e) {
                is IOException -> news.postValue(Resource.Error(StringConstants.network_failure))
                else -> news.postValue(Resource.Error(StringConstants.conversion_error))
            }
        }
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        try {
            if (checkInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, pageNumber)
                searchNews.postValue(handleNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error(StringConstants.network_error))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error(StringConstants.network_failure))
                else -> searchNews.postValue(Resource.Error(StringConstants.conversion_error))
            }
        }
    }

    fun saveNews(article: Article) = viewModelScope.launch {
        newsRepository.insertArticles(article)
    }

    fun getSavedNews() = newsRepository.getSavedArticles()

    fun deleteSavedNews(article: Article) = viewModelScope.launch {
        newsRepository.deleteSavedArticles(article)
    }

    private fun handleNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pageNumber++
                if(newsResponse != null){
                    val oldArticles = newsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles!!)

//                    /Log.d(TAG, "handleNewsResponse: $oldArticles")
                }
                return Resource.Success(newsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    @Suppress("DEPRECATION")
    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}