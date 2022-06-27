package com.meezu.newsapp.ui.fragment

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.meezu.newsapp.R
import com.meezu.newsapp.adapter.NewsAdapter
import com.meezu.newsapp.databinding.FragmentNewsBinding
import com.meezu.newsapp.models.Article
import com.meezu.newsapp.ui.NewsActivity
import com.meezu.newsapp.ui.NewsViewModel
import com.meezu.newsapp.utils.Resource
import com.meezu.newsapp.utils.constants.StringConstants

class NewsFragment : Fragment(), NewsAdapter.ClickListener {

    private lateinit var binding : FragmentNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var newsAdapter: NewsAdapter
    val handler = Handler()

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
            viewModel.getTrendingNews(StringConstants.country_code)
            handler.postDelayed(this, 5 * 60000)
        }
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter(requireContext(), this)
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setViewModelObserver() {

        viewModel.news.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

//        binding.itemErrorMessage.btnRetry.setOnClickListener {
//            viewModel.getTrendingNews(StringConstants.country_code)
//        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.visibility = View.VISIBLE
//        binding.itemErrorMessage.tvErrorMessage.text = message
    }

    override fun onclick(article: Article) {
        sharedViewModel.shareMessage(article)
        findNavController().navigate(R.id.articleFragment)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refresh);
    }

}