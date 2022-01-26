package com.example.nutritionapp.menu.geofences_favorites_tabs

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.databinding.ActiveGeofenceItemBinding
import com.example.nutritionapp.databinding.IngredientItemBinding
import com.example.nutritionapp.databinding.ListOfActiveGeofenceBinding
import com.example.nutritionapp.maps.RecipeNotificationClassDomain

class ActiveGeofencesAdapter(var listener: ActiveGeofenceListener, val removeButton: ActiveGeofenceRemoveButton) : ListAdapter<RecipeNotificationClassDomain, geofenceViewHolder>(ActiveGeofenceDiffCallback()) {

    //Q: Is this the best way to perform a callback on a removed an item from a listAdapter?
    val geofenceToRemove = mutableListOf<RecipeNotificationClassDomain>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): geofenceViewHolder {

        val view : ActiveGeofenceItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            geofenceViewHolder.LAYOUT,
            parent,
            false)
        return geofenceViewHolder(view, listener, removeButton)
    }

    override fun onBindViewHolder(holder: geofenceViewHolder, position: Int) {
        holder.bind(getItem(position))
        //source: https://stackoverflow.com/questions/62808768/how-to-add-remove-the-listadapter-items-in-android
        holder.binding.button.setOnClickListener {
            geofenceToRemove.add(getItem(position))

            val currentList =  this.currentList.toMutableList()
            currentList.removeAt(position)
            this.submitList(currentList)
        }

    }
}

class geofenceViewHolder(val binding : ActiveGeofenceItemBinding,
val clickListener : ActiveGeofenceListener,
val removeListener : ActiveGeofenceRemoveButton) : RecyclerView.ViewHolder(binding.root)
{
    fun bind ( recipeNotificationClassDomain: RecipeNotificationClassDomain)
    {
        binding.recipe = recipeNotificationClassDomain
        binding.itemListener = clickListener
        binding.removeListener = removeListener
    }
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.active_geofence_item
    }
}

class ActiveGeofenceDiffCallback :
    DiffUtil.ItemCallback<RecipeNotificationClassDomain>() {
    override fun areItemsTheSame(
        oldItem: RecipeNotificationClassDomain,
        newItem: RecipeNotificationClassDomain
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RecipeNotificationClassDomain,
        newItem: RecipeNotificationClassDomain
    ): Boolean {
        return oldItem == newItem
    }
}
//Note: There are multiple ways to implement onClick Listener for recyclerViews, this is just one of them
class ActiveGeofenceListener(val clickListener: (activeGeofenceItem: RecipeNotificationClassDomain) -> Unit) {
    //it's a bit strange b/c usually we use the parameter inside the class but here we are using the body of the class
    //to define the value of the parameter
    fun onClick(activeGeofenceItem: RecipeNotificationClassDomain) {clickListener(activeGeofenceItem)
    }
}
class ActiveGeofenceRemoveButton(val clickListener: (activeGeofenceItem: RecipeNotificationClassDomain) -> Unit) {

    fun onClick(activeGeofenceItem: RecipeNotificationClassDomain) = clickListener(activeGeofenceItem)
}