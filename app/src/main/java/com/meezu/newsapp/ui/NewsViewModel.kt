package com.meezu.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meezu.newsapp.models.Article
import com.meezu.newsapp.models.NewsResponse
import com.meezu.newsapp.repository.NewsRepository
import com.meezu.newsapp.utils.NewsApplication
import com.meezu.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    application: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(application){

    val news: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    init {
        getTrendingNews("us")
    }

    fun getTrendingNews(countryCode: String) = viewModelScope.launch {
        news.postValue(Resource.Loading())
        try {
            if(checkInternetConnection()) {
                val response = newsRepository.getTrendingNews(countryCode)
                news.postValue(handleNewsResponse(response))
            } else {
                news.postValue(Resource.Error("No internet connection"))
            }
        } catch(e: Throwable) {
            when(e) {
                is IOException -> news.postValue(Resource.Error("Network Failure"))
                else -> news.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun getAllNews() = viewModelScope.launch {
        news.postValue(Resource.Loading())
        val response = newsRepository.getAllNews()
        news.postValue(handleNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        try {
            if(checkInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery)
                searchNews.postValue(handleNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun saveNews(article: Article) = viewModelScope.launch {
        newsRepository.insertArticles(article)
    }

    fun getSavedNews() = newsRepository.getSavedArticles()

    fun deleteSavedNews(article: Article) = viewModelScope.launch{
        newsRepository.deleteSavedArticles(article)
    }

    private fun handleNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    @Suppress("DEPRECATION")
    private fun checkInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
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