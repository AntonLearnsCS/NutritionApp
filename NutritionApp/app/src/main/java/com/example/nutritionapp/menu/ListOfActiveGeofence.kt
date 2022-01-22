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
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.ListOfActiveGeofenceBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
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
        val listOfRecipeNotification = viewModel.getAllRecipeNotification()


        listOfActiveGeofenceBinding = DataBindingUtil.inflate(inflater, R.layout.list_of_active_geofence,container, false)
        Log.i("geofenceList","value: ${viewModel.listOfIntent[0]?.extras?.get("RecipeNotificationClass")}")
        val item = viewModel.listOfIntent[0]?.extras?.get("RecipeNotificationClass")//?.extras?.get("RecipeNotificationClass") as RecipeNotificationClassDomain
        listOfActiveGeofenceBinding.textView2.text = item.toString() ///intent.getSerializableExtra("RecipeNotificationClass") as RecipeNotificationClass

        listOfActiveGeofenceBinding.textView3.text = viewModel.listOfPendingIntent[0].toString()
        removeGeofences()
        return listOfActiveGeofenceBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contxt = context
    }
    private fun removeGeofences() {
        val requestId = viewModel.listOfIntent[0]?.extras?.get("RecipeNotificationClass") as RecipeNotificationClassDTO
        val myList = mutableListOf<String>(requestId.mId)
        geofencingClient.removeGeofences(myList)?.run {
            addOnSuccessListener {
                Toast.makeText(contxt, "removed geofence", Toast.LENGTH_SHORT)
                    .show()
            }
            addOnFailureListener {
                Log.d("TAG", "geofences_not_removed")
            }
        }
    }
}
