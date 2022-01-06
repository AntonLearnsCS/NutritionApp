
package com.example.nutritionapp.recipe.intolerancespinnerclasses
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import com.example.nutritionapp.databinding.IntoleranceOptionItemBinding

data class IntoleraceDataType(val option : String, var isChecked : Boolean = false)

class IntoleranceAdapter(context: Context, resource: Int, textResource:Int, list: ArrayList<IntoleraceDataType>) :
    ArrayAdapter<IntoleraceDataType>(context, resource, textResource, list){
    val selectedIntoleranceList = mutableListOf<String>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return customAdapterInit(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return customAdapterInit(position, convertView, parent)
    }

    fun customAdapterInit(position: Int, convertView: View?, parent: ViewGroup) : View {
        val inflater = LayoutInflater.from(parent.context)
        val intoleranceBinding = IntoleranceOptionItemBinding.inflate(inflater,parent,false)

        val item : IntoleraceDataType? = getItem(position)
        //sets first item in spinner as placeholder for spinner title: "Food Allergens:"
        if (position == 0)
        {
            intoleranceBinding.isChecked.visibility = View.INVISIBLE
        }
        intoleranceBinding.intoleranceNameVariable = item

        intoleranceBinding.isChecked.setOnClickListener {
            if (intoleranceBinding.isChecked.isChecked && intoleranceBinding.intoleranceName.text != "Food Allergens:")
            {
                item!!.isChecked = true
                intoleranceBinding.executePendingBindings()
                selectedIntoleranceList.add(item.option)
            }
            else
            {
                selectedIntoleranceList.remove(item!!.option)
            }
            Log.i("test","size: ${selectedIntoleranceList.size}")
        }
        return intoleranceBinding.root
    }
}
