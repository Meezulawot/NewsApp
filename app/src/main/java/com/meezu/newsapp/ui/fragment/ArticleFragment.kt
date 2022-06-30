package com.meezu.newsapp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.meezu.newsapp.R
import com.meezu.newsapp.databinding.FragmentArticleBinding
import com.meezu.newsapp.data.models.Article
import com.meezu.newsapp.ui.viewmodel.NewsViewModel
import com.meezu.newsapp.ui.viewmodel.SharedViewModel
import com.meezu.newsapp.utils.constants.StringConstants

class ArticleFragment : Fragment() {

    var TAG = ArticleFragment::class.java.simpleName
    private lateinit var binding : FragmentArticleBinding
    private lateinit var viewModel : NewsViewModel
    private lateinit var sharedViewModel: SharedViewModel
    var article: Article? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.sharedArticle.observe(viewLifecycleOwner, Observer {
            binding.articleData = it
            article = it
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchItem.isVisible = false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT,article!!.source!!.name)

                    val body = article!!.title + "\n" + article!!.url
                    intent.putExtra(Intent.EXTRA_TEXT,body)
                    startActivity(Intent.createChooser(intent, "Share with: "));

                }catch (e: Exception){
                    Toast.makeText(context, StringConstants.share_exception_message, Toast.LENGTH_SHORT).show()
                }
            }
            R.id.action_save -> {
                viewModel.saveNews(article!!)
                item.icon = resources.getDrawable(R.drawable.ic_bookmarked);
                Snackbar.make(requireView(), StringConstants.saved_message, Snackbar.LENGTH_SHORT).show()
            }
            else -> super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item);
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE
    }

}