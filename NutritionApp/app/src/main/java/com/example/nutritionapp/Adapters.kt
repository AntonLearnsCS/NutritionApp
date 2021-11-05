package com.example.nutritionapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.w3c.dom.Text
import java.lang.StringBuilder

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

    @JvmStatic
    @BindingAdapter("listMissingIngredients")
    fun listIngredients(textView: TextView, listMissingIngredients : LiveData<List<String>>?)
    {
        val sb = StringBuilder()

        if (textView.tag != "recipe_layout_tag")
        sb.append("Missing Ingredients:\n")

        if(!listMissingIngredients?.value.isNullOrEmpty())
        {
            for (item in listMissingIngredients?.value!!)
            {
                sb.append("• ${item}\n")
            }
        }
        else
            sb.append("None")
        textView.text = sb
    }

    @JvmStatic
    @BindingAdapter("app:foodInText")
    fun foodInText(editText: EditText, listMissingIngredients : MutableLiveData<String>?)
    {
    /*    if (boolean)
        {
            editText.text.append("boolean")
            return
        }*/
        val sb = StringBuilder()

        if (!listMissingIngredients?.value.isNullOrEmpty()) {
            val strs = listMissingIngredients?.value?.split(",")?.toTypedArray()

            if (strs != null) {
                for (i in strs) {
                    sb.append("$i\n")
                }
            }
        }
        else
            sb.append("")

        if (!editText.text.equals(listMissingIngredients?.value)) {
            editText.text.clear()
            editText.text.append(sb)
        }
    }
    //Note: Suffix must be "AttrChanged"

    @JvmStatic
    @BindingAdapter("foodInTextAttrChanged")
    fun setListener( editText : EditText, listener : InverseBindingListener) {
        val textWatcher : TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("test","beforeTextChanged called")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i("test","onTextChanged called")
            }

            override fun afterTextChanged(s: Editable?) {
                listener.onChange()
            }
        }
        editText.addTextChangedListener(  textWatcher
        )
    }

    @InverseBindingAdapter(attribute = "foodInText")
    @JvmStatic
    fun getText(view: EditText) : String {
        return view.text.toString()
    }



    @JvmStatic
    @BindingAdapter("displaySteps")
    fun bindSteps(textView: TextView, listSteps : LiveData<List<String>>)
    {

        Log.i("test","displaySteps called: ${listSteps.value?.size}")
        if (!listSteps.value.isNullOrEmpty()) {
            Log.i("test","displaySteps not empty")

            //Note: We can't use String.plus() to append strings to a base string i.e val mString.plus(mList[0]) since Strings are final objects
            val mString = listSteps.value!!.joinToString(separator = "\n\n")
            textView.text = mString
        }
        else
            textView.text = R.string.no_instructions_provided.toString()
    }

    @JvmStatic
    @BindingAdapter("listIngredients")
    fun listIngredients(textView: TextView, mString: String)
    {
        val sb = StringBuilder()

        //convert string to list
        val result: List<String> = mString.split(",").map { it.trim() }

        for (i in result)
        {
            sb.append("$i\n")
        }

        textView.text = sb
    }



    //animate changing the view visibility
    fun View.fadeIn() {
        this.visibility = View.VISIBLE
        this.alpha = 0f
        this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeIn.alpha = 1f
            }
        })
    }

    //animate changing the view visibility
    fun View.fadeOut() {
        this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeOut.alpha = 1f
                this@fadeOut.visibility = View.GONE
            }
        })
    }

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }
}