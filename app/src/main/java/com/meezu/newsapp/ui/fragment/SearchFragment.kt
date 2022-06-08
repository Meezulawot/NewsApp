package com.meezu.newsapp.ui.fragment

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.meezu.newsapp.R
import com.meezu.newsapp.adapter.NewsAdapter
import com.meezu.newsapp.databinding.FragmentSearchBinding
import com.meezu.newsapp.models.Article
import com.meezu.newsapp.ui.NewsActivity
import com.meezu.newsapp.ui.NewsViewModel
import com.meezu.newsapp.utils.Resource
import com.meezu.newsapp.utils.constants.StringConstants
import java.util.*


class SearchFragment : Fragment(), NewsAdapter.ClickListener {

    private lateinit var binding : FragmentSearchBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "";
        viewModel = (activity as NewsActivity).viewModel
        setRecyclerView()
        setViewModelObserver()

    }

    private fun setViewModelObserver() {
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
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
                        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                        Log.e("SearchFragment", message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter(requireContext(), this)
        binding.rvNews.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvNews.adapter = newsAdapter
    }

    override fun onclick(article: Article) {
        val bundle = Bundle()
        bundle.putSerializable(StringConstants.Article, article)
        findNavController().navigate(R.id.articleFragment, bundle)
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