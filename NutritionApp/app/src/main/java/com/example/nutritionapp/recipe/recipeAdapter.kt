package com.example.nutritionapp.recipe

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeLayoutItemBinding


class recipeAdapter(val onClickListener: RecipeIngredientListener) : ListAdapter<RecipeIngredientResultDomain, recipeAdapter.ViewHolder>(
    LocalIngredienttDiffCallback()
)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : RecipeLayoutItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ViewHolder.LAYOUT,
            parent,
            false
        )
        return ViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: recipeAdapter.ViewHolder, position: Int) {
        val ingredientItem = getItem(position)
        holder.bind(ingredientItem)
    }

    class ViewHolder(
        val binding: RecipeLayoutItemBinding,
        val clickListener: RecipeIngredientListener
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: RecipeIngredientResultDomain)
        {
            binding.clickListener = clickListener
            binding.recipe = item
        }
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.recipe_layout_item
        }
    }

    class LocalIngredienttDiffCallback :
        DiffUtil.ItemCallback<RecipeIngredientResultDomain>() {
        override fun areItemsTheSame(
            oldItem: RecipeIngredientResultDomain,
            newItem: RecipeIngredientResultDomain
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RecipeIngredientResultDomain,
            newItem: RecipeIngredientResultDomain
        ): Boolean {
            return oldItem == newItem
        }
    }
    //Note: There are multiple ways to implement onClick Listener for recyclerViews, this is just one of them
    class RecipeIngredientListener(val clickListener: (recipeDomain: RecipeIngredientResultDomain) -> Unit) {
        //it's a bit strange b/c usually we use the parameter inside the class but here we are using the body of the class
        //to define the value of the parameter
        fun onClick(recipeDomain: RecipeIngredientResultDomain){
            Log.i("test","id: ${recipeDomain.id}")
            clickListener(recipeDomain)}
    }
}