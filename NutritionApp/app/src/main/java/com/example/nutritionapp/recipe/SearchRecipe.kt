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
//viewModel is being initialized before the detectFoodText is done running in the background
 //val viewModel by lazy { ViewModelProvider(this).get(IngredientViewModel::class.java)}
val viewModel : IngredientViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        //Q: How to inflate layout object in onViewCreated?
        val args = arguments?.getString("ListOfIngredients")
        Log.i("test","args: $args")
        val mockText = "Apple,Oranges,Kiwi"
        Log.i("test","LiveData: ${viewModel.listOfRecipesLiveData?.value}")
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.recipe_layout, container,false)

        binding.viewModel = viewModel

        binding.searchRecipeButton.setOnClickListener {
            viewModel.findRecipeByIngredients()
        }

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = recipeAdapter(recipeAdapter.RecipeIngredientListener { recipe -> viewModel.setNavigateToRecipe(recipe) })

        binding.recipeRecyclerView.adapter = adapter

        viewModel.listOfRecipesLiveData?.observe(viewLifecycleOwner, Observer{
            adapter.submitList(it)
        })

        viewModel.navigateToRecipe.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                findNavController().navigate(SearchRecipeDirections.actionSearchRecipeToRecipeDetail(it))
                viewModel.setNavigateToRecipeNull()
            }

        })

        return binding.root
    }
}