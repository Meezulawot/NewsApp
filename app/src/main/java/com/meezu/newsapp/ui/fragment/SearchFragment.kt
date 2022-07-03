package com.meezu.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meezu.newsapp.R
import com.meezu.newsapp.ui.adapter.NewsAdapter
import com.meezu.newsapp.databinding.FragmentSearchBinding
import com.meezu.newsapp.data.models.Article
import com.meezu.newsapp.ui.listener.ClickListener
import com.meezu.newsapp.ui.viewmodel.NewsViewModel
import com.meezu.newsapp.ui.viewmodel.SharedViewModel
import com.meezu.newsapp.utils.Resource
import com.meezu.newsapp.utils.constants.StringConstants


class SearchFragment : Fragment(), ClickListener {

    var TAG = SearchFragment::class.java.simpleName
    private lateinit var binding : FragmentSearchBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "";
        viewModel = ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        setRecyclerView()
        setViewModelObserver()

    }

    private fun setViewModelObserver() {
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles!!.toList())
                        val totalPages = newsResponse.totalResults!! / 10  + 2
                        isLastPage = viewModel.pageNumber == totalPages
                        if(isLastPage) {
                            binding.rvNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("SearchFragment", message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    var isLoading : Boolean = false
    var isLastPage : Boolean = false
    var isScrolling : Boolean = false

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount: Int = layoutManager.childCount
            val totalItemCount: Int = layoutManager.itemCount
            val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()

            if (!isLoading && !isLastPage && isScrolling) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                    && firstVisibleItemPosition >= 0 && totalItemCount >= 10
                ) {
                    viewModel.getTrendingNews(StringConstants.country_code)
                    isScrolling = false
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter(this)
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }

    }

    override fun onclick(article: Article) {
        sharedViewModel.shareMessage(article)
        findNavController().navigate(R.id.articleFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu, menu)
        val shareItem = menu.findItem(R.id.action_share)
        val saveItem = menu.findItem(R.id.action_save)
        shareItem.isVisible = false;
        saveItem.isVisible = false;

        val searchViewItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = MenuItemCompat.getActionView(searchViewItem) as SearchView
        searchView.isIconifiedByDefault = false
        searchView.queryHint = StringConstants.search_hint

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.searchNews(query)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.searchNews(newText)
                    return false
                }
            })

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.title = StringConstants.app_name;
    }

}