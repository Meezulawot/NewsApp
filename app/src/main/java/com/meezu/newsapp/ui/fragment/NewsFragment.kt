package com.meezu.newsapp.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.meezu.newsapp.R
import com.meezu.newsapp.data.models.Article
import com.meezu.newsapp.databinding.FragmentNewsBinding
import com.meezu.newsapp.ui.adapter.NewsAdapter
import com.meezu.newsapp.ui.listener.ClickListener
import com.meezu.newsapp.ui.listener.ScrollRecyclerListener
import com.meezu.newsapp.ui.viewmodel.NewsViewModel
import com.meezu.newsapp.ui.viewmodel.SharedViewModel
import com.meezu.newsapp.utils.Resource
import com.meezu.newsapp.utils.constants.StringConstants

class NewsFragment : Fragment(), ClickListener {

    var TAG = NewsFragment::class.java.simpleName
    private lateinit var binding : FragmentNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var newsAdapter: NewsAdapter
    val handler = Handler()
    val page = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        setRecyclerView()
        setViewModelObserver()
        refresh.run()
    }

    private val refresh: Runnable = object : Runnable {
        override fun run() {
            viewModel.getTrendingNews(StringConstants.country_code, page)
            handler.postDelayed(this, 5 * 60000)
        }
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter(this)
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addOnScrollListener(object : ScrollRecyclerListener() {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    viewModel.getTrendingNews(StringConstants.country_code, page)
                    return
                }
            })
        }
    }

    private fun setViewModelObserver() {
        viewModel.news.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.d(TAG, "setViewModelObserver: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

//        binding.itemErrorMessage.btnRetry.setOnClickListener {
//            viewModel.getTrendingNews(StringConstants.country_code, page)
//        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

//    private fun hideErrorMessage() {
//        binding.itemErrorMessage.root.visibility = View.GONE
//    }
//
//    private fun showErrorMessage(message: String) {
//        binding.itemErrorMessage.root.visibility = View.VISIBLE;
//        binding.itemErrorMessage.tvErrorMessage.text = message
//    }
//

    override fun onclick(article: Article) {
        sharedViewModel.shareMessage(article)
        findNavController().navigate(R.id.articleFragment)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refresh)
    }

}