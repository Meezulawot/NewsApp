package com.meezu.newsapp.utils

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    Glide.with(view.context).load(url).into(view)
}

//@BindingAdapter("imageUrlWithPlaceHolder, placeHolder ")
//fun loadImage(view: ImageView, url: String?, placeHolder: Int?) {
//
//    Glide.with(view.context).load(url).into(view)
//}

@BindingAdapter("loadUrl")
fun loadUrl(view: WebView, url: String) {
    view.webViewClient = WebViewClient()
    view.loadUrl(url)
}