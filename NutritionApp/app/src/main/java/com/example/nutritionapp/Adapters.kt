package com.example.nutritionapp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.w3c.dom.Text

class Adapters {
    @BindingAdapter("testSize")
    fun bindStatus(textView: TextView, sizeString: Int) {
        textView.text = sizeString.toString()
    }
}