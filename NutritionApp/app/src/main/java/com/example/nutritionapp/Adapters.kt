package com.example.nutritionapp

import android.app.Application
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.test.core.app.ApplicationProvider
import org.w3c.dom.Text

class Adapters {
    @BindingAdapter("testSize")
    fun bindStatus(textView: TextView, sizeString: Int) {
        if (sizeString > 0)
        textView.text = sizeString.toString()
        else
            textView.setText(R.string.app_name)
    }
}