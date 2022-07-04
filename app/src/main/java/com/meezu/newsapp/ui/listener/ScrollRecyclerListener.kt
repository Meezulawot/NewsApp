package com.meezu.newsapp.ui.listener

import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class ScrollRecyclerListener : RecyclerView.OnScrollListener() {

    private var previousTotalItemCount = 0
    private var loading = true
    private val visibleThreshold = 0
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private val startingPageIndex = 0
    private var currentPage = 0

    override fun onScrolled(mRecyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(mRecyclerView, dx, dy)
        val mLayoutManager = mRecyclerView
            .layoutManager as LinearLayoutManager?
        visibleItemCount = mRecyclerView.childCount
        totalItemCount = mLayoutManager!!.itemCount
        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()
        onScroll(firstVisibleItem, visibleItemCount, totalItemCount)
    }

    fun onScroll(firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
            currentPage++
        }

        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem +
            visibleThreshold
        ) {
            onLoadMore(currentPage + 1, totalItemCount)
            loading = true
        }
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int)

}