package com.example.nutritionapp.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeLayoutBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchRecipe : Fragment() {
private lateinit var binding : RecipeLayoutBinding
//viewModel is being initialized before the detectFoodText is done running in the background
 //val viewModel by lazy { ViewModelProvider(this).get(IngredientViewModel::class.java)}

private val adapter = recipeAdapter(recipeAdapter.RecipeIngredientListener { recipe ->
        Log.i("test","Recipe selected name: ${recipe.title}")
        viewModel.setNavigateToRecipe(recipe)
        viewModel.setNavigateToRecipeFlag(true)
    //need a seperate flag
})

val viewModel by sharedViewModel<IngredientViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        //Q: How to inflate layout object in onViewCreated?
        //val args = SearchRecipeArgs.fromBundle(requireArguments()).ListOfIngredients//arguments?.getString("ListOfIngredients")
        //Log.i("test","args: $args")

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
            if (it) {
                    viewModel.getRecipeInstructions()
                //TODO: Placing the navigation step outside of the flag results in the destination fragment's viewModel
                // not having the updated value.
                findNavController().navigate(
                    SearchRecipeDirections.actionSearchRecipeToRecipeDetail(
                        viewModel.navigateToRecipe.value!!))

                Log.i("test","food in text size: ${viewModel.foodInText.size}")
                //Once getRecipeInstructions() is complete it will set mFlag = true so that navigation happens only after the liveData in getRecipeInstructions is updated
                viewModel.mFlag.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        //TODO: navigation initially inside this flag call; results in the viewModel inside the destination having
                        // the updated values

                        viewModel.setNavigateToRecipeFlag(false)
                    }
                })
            }
        }
        )

        //source: https://stackoverflow.com/questions/55074497/how-to-add-onbackpressedcallback-to-fragment
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.i("test","SearchRecipe on back pressed")
                    viewModel.searchRecipeEditTextFlag.value = true
                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setMissingIngredientsNull()
    }
}
