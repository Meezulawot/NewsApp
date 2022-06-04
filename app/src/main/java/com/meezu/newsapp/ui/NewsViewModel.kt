package com.meezu.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meezu.newsapp.models.NewsResponse
import com.meezu.newsapp.repository.NewsRepository
import com.meezu.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel(){

    val news: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    init {
        getTrendingNews("us")
    }

    private fun getTrendingNews(countryCode: String) = viewModelScope.launch {
        news.postValue(Resource.Loading())
        val response = newsRepository.getTrendingNews(countryCode)
        news.postValue(handleNewsResponse(response))
    }

    private fun getAllNews() = viewModelScope.launch {
        news.postValue(Resource.Loading())
        val response = newsRepository.getAllNews()
        news.postValue(handleNewsResponse(response))
    }

    private fun handleNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}