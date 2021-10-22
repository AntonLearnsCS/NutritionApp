package com.example.nutritionapp.recipe

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.IngredientItemLocalBinding
import com.example.nutritionapp.databinding.RecipeLayoutBinding
import com.example.nutritionapp.recipe.ListSelectedIngredients
import org.koin.android.ext.android.inject


class recipeAdapter(val onClickListener: RecipeIngredientListener) : ListAdapter<RecipeIngredientResult, recipeAdapter.ViewHolder>(
    LocalIngredienttDiffCallback()
)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : RecipeLayoutBinding = DataBindingUtil.inflate(
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
        val binding: RecipeLayoutBinding,
        val clickListener: RecipeIngredientListener
    ) : RecyclerView.ViewHolder(binding.root)
    {

        fun bind(item: RecipeIngredientResult)
        {
            binding.clickListener = clickListener
            binding.recipe = item
        }
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.ingredient_item_local
        }
    }

    class LocalIngredienttDiffCallback :
        DiffUtil.ItemCallback<RecipeIngredientResult>() {
        override fun areItemsTheSame(
            oldItem: RecipeIngredientResult,
            newItem: RecipeIngredientResult
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RecipeIngredientResult,
            newItem: RecipeIngredientResult
        ): Boolean {
            return oldItem == newItem
        }
    }
    //Note: There are multiple ways to implement onClick Listener for recyclerViews, this is just one of them
    class RecipeIngredientListener(val clickListener: (recipe: RecipeIngredientResult) -> Unit) {
        //it's a bit strange b/c usually we use the parameter inside the class but here we are using the body of the class
        //to define the value of the parameter
        fun onClick(recipe: RecipeIngredientResult) = clickListener(recipe)
    }
}