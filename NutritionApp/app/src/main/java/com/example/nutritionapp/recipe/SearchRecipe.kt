package com.example.nutritionapp.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeLayoutBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import org.koin.android.ext.android.inject

class SearchRecipe : Fragment() {
private lateinit var binding : RecipeLayoutBinding
 val viewModel : IngredientViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        //Q: How to inflate layout object in onViewCreated?
        val args = arguments?.getSerializable("SelectedIngredients") as ListSelectedIngredients
        val mockText = "Apple,Oranges,Kiwi"
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.recipe_layout, container,false)
        binding.searchRecipeButton.setOnClickListener {
            viewModel.findRecipeByIngredients()
        }
        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = recipeAdapter(recipeAdapter.RecipeIngredientListener { recipe -> viewModel.setNavigateToRecipe(recipe) })

        binding.recipeRecyclerView.adapter = adapter

        viewModel.listOfRecipesLiveData.observe(viewLifecycleOwner, Observer{
            adapter.submitList(it)
        })
        viewModel.navigateToRecipe.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                findNavController().navigate()
            }

        })

        return binding.root
    }
}