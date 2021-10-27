package com.example.nutritionapp.recipe.recipedetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeDetailBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.recipe.RecipeIngredientResult
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RecipeDetail : Fragment() {
    private lateinit var binding : RecipeDetailBinding
    //"by inject()" delegate is used to lazily inject dependencies
     val viewModel: IngredientViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail,container,false)
        binding.viewModel = viewModel
        val args  = RecipeDetailArgs.fromBundle(requireArguments()).Recipe
        binding.recipe = args
        Log.i("test","args: ${args.title}")
        Log.i("test","RecipeDetail: ${viewModel.navigateToRecipe.value?.title}")

        //TODO: In contrast, calling getRecipeInstructions here does not update the xml
        viewModel.getRecipeInstructions()

        return binding.root
    }
}