package com.example.nutritionapp.ingredientlist


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.databinding.IngredientItemBinding

class networkIngredientAdapter (val clickListener : NetworkIngredientListener) : ListAdapter<IngredientDataClass, networkIngredientAdapter.ViewHolder>(NetworkIngredienttDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : IngredientItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ViewHolder.LAYOUT,
            parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredientItem = getItem(position)
        holder.bind(ingredientItem,clickListener)
    }


    //implement data binding to avoid using findViewById()
    class ViewHolder(val binding : IngredientItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item : IngredientDataClass, clickListener: NetworkIngredientListener)
        {
            //So now every viewHolder has an instance of NetworkIngredientListener
            binding.clickListener = clickListener
            binding.ingredientItem = item
        }
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.ingredient_item
        }
    }

    class NetworkIngredienttDiffCallback :
        DiffUtil.ItemCallback<IngredientDataClass>() {
        override fun areItemsTheSame(
            oldItem: IngredientDataClass,
            newItem: IngredientDataClass
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: IngredientDataClass,
            newItem: IngredientDataClass
        ): Boolean {
            return oldItem == newItem
        }
    }
    //Note: There are multiple ways to implement onClick Listener for recyclerViews, this is just one of them
    class NetworkIngredientListener(val clickListener: (ingredientItem: IngredientDataClass) -> Unit) {
        //it's a bit strange b/c usually we use the parameter inside the class but here we are using the body of the class
        //to define the value of the parameter
        fun onClick(ingredient: IngredientDataClass) = clickListener(ingredient)
    }
}