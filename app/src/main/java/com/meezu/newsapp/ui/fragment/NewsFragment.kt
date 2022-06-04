package com.meezu.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.meezu.newsapp.R
import com.meezu.newsapp.adapter.NewsAdapter
import com.meezu.newsapp.databinding.FragmentNewsBinding
import com.meezu.newsapp.models.Article
import com.meezu.newsapp.ui.NewsActivity
import com.meezu.newsapp.ui.NewsViewModel
import com.meezu.newsapp.utils.Resource
import com.meezu.newsapp.utils.constants.StringConstants

class NewsFragment : Fragment(), NewsAdapter.ClickListener{

    private lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel : NewsViewModel
    private lateinit var newsAdapter : NewsAdapter
    private var article: Article? = null

    val TAG = "NewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentNewsBinding.inflate(layoutInflater, container, false)
        return(binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setRecyclerView()
        setViewModelObserver()
    }

    private fun setViewModelObserver(){
        viewModel.news.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "Error: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setRecyclerView(){
        newsAdapter = NewsAdapter(requireContext(), this)
        binding.rvNews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvNews.adapter = newsAdapter
    }

    override fun onclick(article: Article) {
//        Toast.makeText(context, article.url, Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        bundle.putString(StringConstants.Article_URL, article.url)
        findNavController().navigate(R.id.articleFragment, bundle)
    }
}