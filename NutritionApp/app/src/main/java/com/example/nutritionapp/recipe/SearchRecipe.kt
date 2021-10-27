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

private val adapter = recipeAdapter(recipeAdapter.RecipeIngredientListener { recipe ->
        Log.i("test","Recipe selected name: ${recipe.title}")
        viewModel.setNavigateToRecipe(recipe)
        viewModel.setNavigateToRecipeFlag(true)
}
        )

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
            viewModel.findRecipeByIngredients()}

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(context)


        binding.recipeRecyclerView.adapter = adapter

        viewModel.listOfRecipesLiveData?.observe(viewLifecycleOwner, Observer{
            adapter.submitList(it)
        })

        viewModel.navigateToRecipeFlag.observe(viewLifecycleOwner, Observer {
            if (it)
            {
                //TODO: Calling getRecipeInstructions() in addition to using a flag results in expected behavior
                viewModel.getRecipeInstructions()
                //Once getRecipeInstructions() is complete it will set mFlag = true so that navigation happens only after the liveData in getRecipeInstructions is updated
                viewModel.mFlag.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        findNavController().navigate(
                            SearchRecipeDirections.actionSearchRecipeToRecipeDetail(
                                viewModel.navigateToRecipe.value!!
                            )
                        )
                        viewModel.setNavigateToRecipeFlag(false)
                    }
                })

                }
            }
        )


        return binding.root
    }
}