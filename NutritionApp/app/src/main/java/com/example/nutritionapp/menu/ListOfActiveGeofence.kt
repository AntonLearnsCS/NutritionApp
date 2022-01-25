package com.example.nutritionapp.menu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.ListOfActiveGeofenceBinding
import com.example.nutritionapp.generated.callback.OnClickListener
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
import com.example.nutritionapp.recipe.RecipeIngredientResult
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ListOfActiveGeofence : Fragment() {
private lateinit var listOfActiveGeofenceBinding : ListOfActiveGeofenceBinding
private lateinit var geofencingClient: GeofencingClient
private lateinit var contxt: Context

val viewModel by sharedViewModel<IngredientViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        geofencingClient = LocationServices.getGeofencingClient(contxt)

        //gets list of recipeNotification from Room
        viewModel.getAllRecipeNotification()


        listOfActiveGeofenceBinding = DataBindingUtil.inflate(inflater, R.layout.list_of_active_geofence,container, false)
//        Log.i("geofenceList","value: ${viewModel.listOfIntent[0]?.extras?.get("RecipeNotificationClass")}")
        val adapter = ActiveGeofencesAdapter(ActiveGeofenceListener { activeGeofenceItem ->
            viewModel.setMissingIngredients(activeGeofenceItem.missingIngredients.split(",")
                .map{it.trim()})

            val recipeIngredientResult = RecipeIngredientResult(activeGeofenceItem.id,activeGeofenceItem.recipeName,
                activeGeofenceItem.image ?: "https://www.ecosia.org/images?q=image%20not%20found%20image#id=D0EC9C87026051CB60DE02C9D3264B5AD547EDB2", "JPEG")

            //sets arg for navigation from this fragment to recipeDetail
            viewModel.setNavigateToRecipe(recipeIngredientResult)

            //gets the recipe instructions for the selected active geofence recipe
            viewModel.getRecipeInstructions()

            //nested flag, "mFlag" is true when getRecipeInstructions() is finished running
            viewModel.mFlag.observe(viewLifecycleOwner, Observer { flag ->
                if (flag)
                {
                    Log.i("test","selectedActiveGeofence: id: ${viewModel.navigateToRecipe.value!!.id}")
                    findNavController().navigate(ListOfActiveGeofenceDirections
                        .actionListOfActiveGeofenceToRecipeDetail(viewModel.navigateToRecipe.value!!))
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
            geofencingClient.removeGeofences(tempList)?.run {
                addOnSuccessListener {
                    Toast.makeText(contxt, "removed reminder", Toast.LENGTH_SHORT)
                        .show()
                }
                addOnFailureListener {
                    Log.d("TAG", "geofences_not_removed")
                }
            }
        })

        //only submit list when call to getAllRecipeNotification is completed

            viewModel.listOfRecipeNotification.observe(viewLifecycleOwner, Observer { list ->
                adapter.submitList(list)
            })

        listOfActiveGeofenceBinding.activeGeofenceRecyclerview.adapter = adapter



        return listOfActiveGeofenceBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contxt = context
    }

    private fun removeGeofences() {
        val requestId = viewModel.listOfIntent[0]?.extras?.get("RecipeNotificationClass") as RecipeNotificationClassDTO
        val myList = mutableListOf<Int>(requestId.id)
    }
}
