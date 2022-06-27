package com.meezu.newsapp.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meezu.newsapp.models.Article

class SharedViewModel : ViewModel(){

    var sharedArticle = MutableLiveData<Article>()

    fun shareMessage(article: Article){
        sharedArticle.value = article
    }

}