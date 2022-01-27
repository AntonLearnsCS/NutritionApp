package com.example.nutritionapp.menu.geofences_favorites_tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.FavoriteRecipesFragmentBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
import com.example.nutritionapp.recipe.RecipeIngredientResultDomain
import com.example.nutritionapp.recipe.RecipeIngredientResultNetwork
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FavoriteRecipes : Fragment() {
    private val viewModel by sharedViewModel<IngredientViewModel>()
    private lateinit var binding: FavoriteRecipesFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorite_recipes_fragment, container, false)


        val adapter = FavoriteRecipesAdapter(com.example.nutritionapp.menu.geofences_favorites_tabs.ActiveGeofenceListener { activeGeofenceItem ->

            val recipeIngredientResult = RecipeIngredientResultDomain(activeGeofenceItem.id,activeGeofenceItem.recipeName,
                activeGeofenceItem.image ?: "https://www.ecosia.org/images?q=image%20not%20found%20image#id=D0EC9C87026051CB60DE02C9D3264B5AD547EDB2", "JPEG")

            //sets arg for navigation from this fragment to recipeDetail
            viewModel.setNavigateToRecipe(recipeIngredientResult)

            //gets the recipe instructions for the selected active geofence recipe
            viewModel.getRecipeInstructions()


            //nested flag, "mFlag" is true when getRecipeInstructions() is finished running
            viewModel.mFlag.observe(viewLifecycleOwner, Observer { flag ->
                if (flag)
                {
                    Log.i("test","selectedActiveGeofence: id: ${viewModel.navigateToRecipeNetwork.value!!.id}")
                    findNavController().navigate(
                        ListOfActiveGeofenceDirections
                        .actionListOfActiveGeofenceToRecipeDetail(viewModel.navigateToRecipeNetwork.value!!))
                }
            })

            //viewModel.setSelectedActiveGeofence(recipeIngredientResult)

            Log.i("test","geofence item: ${activeGeofenceItem.recipeName}")
            //reusing the same navigation mechanism from searchRecipe to recipeDetail
            viewModel.setNavigateToRecipeFlag(true)
            //since there is no user input of ingredients at this fragment
            viewModel.foodInText.clear()

        }, ActiveGeofenceRemoveButton { geofence ->
            val tempList = mutableListOf<String>(geofence.id.toString())
        })

        Log.i("test","listOfRecipeIngredientResult: ${viewModel.listOfRecipeIngredientResult.value}")
        viewModel.listOfRecipeIngredientResult.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.map { RecipeNotificationClassDomain(id = it.id, image = it.image, recipeName = it.title,
                missingIngredients = ""
            ) })
        })

        binding.favoriteRecipesRecyclerview.adapter = adapter
        return binding.root
    }
}