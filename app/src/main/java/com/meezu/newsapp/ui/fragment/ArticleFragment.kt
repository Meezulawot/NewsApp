package com.meezu.newsapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.meezu.newsapp.R
import com.meezu.newsapp.databinding.FragmentArticleBinding
import com.meezu.newsapp.models.Article
import com.meezu.newsapp.utils.constants.StringConstants

class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentArticleBinding.inflate(layoutInflater, container, false)

        val url = arguments?.getString(StringConstants.Article_URL)
        setWebView(url!!)

        return (binding.root)
    }

    private fun setWebView(url: String){
        binding.webView.apply{
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    }

}