package com.meezu.newsapp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.meezu.newsapp.R
import com.meezu.newsapp.databinding.FragmentArticleBinding
import com.meezu.newsapp.databinding.FragmentNewsBinding
import com.meezu.newsapp.models.Article
import com.meezu.newsapp.ui.NewsActivity
import com.meezu.newsapp.ui.NewsViewModel
import com.meezu.newsapp.utils.constants.StringConstants

class ArticleFragment : Fragment() {

    private lateinit var binding : FragmentArticleBinding
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

        binding.webView.apply{
            webViewClient = WebViewClient()
            loadUrl(article!!.url)
        }
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
                    intent.putExtra(Intent.EXTRA_SUBJECT, article!!.source!!.name)

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