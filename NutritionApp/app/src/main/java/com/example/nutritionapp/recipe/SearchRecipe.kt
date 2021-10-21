package com.example.nutritionapp.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeLayoutBinding

class SearchRecipe : Fragment() {
private lateinit var binding : RecipeLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        //Q: How to inflate layout object in onViewCreated?
        val args = arguments?.getSerializable("SelectedIngredients") as ListSelectedIngredients

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.recipe_layout, container,false)
        return binding.root
    }
}