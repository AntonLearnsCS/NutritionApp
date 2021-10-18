package com.example.nutritionapp

import android.app.Application
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.w3c.dom.Text

object Adapters {
    @JvmStatic
    @BindingAdapter("testSize")
    fun bindStatus(textView: TextView, sizeString: Int) {
        if (sizeString > 0)
        textView.text = sizeString.toString()
        else
            textView.setText(R.string.app_name)
    }
    //Note: Glide takes care of image buffering and caching for us
    @JvmStatic
    @BindingAdapter("displayImage")
    fun bindImage(imageView: ImageView, url: String?) {
        url?.let {
            val imgUri = url.toUri().buildUpon().scheme("https").build()
            Glide.with(imageView.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imageView)
        }
    }
}