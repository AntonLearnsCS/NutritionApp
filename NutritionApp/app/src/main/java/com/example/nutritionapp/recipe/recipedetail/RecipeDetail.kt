package com.example.nutritionapp.recipe.recipedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeDetailBinding
import com.example.nutritionapp.recipe.RecipeIngredientResult

class RecipeDetail : Fragment() {
    private lateinit var binding : RecipeDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail,container,false)

        val args = RecipeDetailArgs.fromBundle(requireArguments()).Recipe
        binding.recipe = args

        return binding.root
    }
}