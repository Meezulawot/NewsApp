package com.meezu.newsapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.meezu.newsapp.R
import com.meezu.newsapp.databinding.FragmentArticleBinding
import com.meezu.newsapp.models.Article
import com.meezu.newsapp.ui.NewsActivity
import com.meezu.newsapp.ui.NewsViewModel
import com.meezu.newsapp.utils.constants.StringConstants

class ArticleFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentArticleBinding
    private lateinit var viewModel : NewsViewModel

    private var article: Article? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(layoutInflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        article = arguments?.getSerializable(StringConstants.Article) as Article
        setWebView(article!!)
        initListener()
    }

    private fun setWebView(article: Article){
        binding.webView.apply{
            webViewClient = WebViewClient()
            loadUrl(article.url!!)
        }
    }

    private fun initListener(){
        binding.imgSave.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0){
            binding.imgSave -> {
                viewModel.saveNews(article!!)
                Snackbar.make(requireView(), "Article has been saved", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE
    }
}