package com.example.nutritionapp.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.IngredientItemBinding
import com.example.nutritionapp.databinding.ListOfActiveGeofenceBinding

class ActiveGeofencesAdapter : ListAdapter<GeofenceReferenceData, geofenceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): geofenceViewHolder {

        val view : ListOfActiveGeofenceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            geofenceViewHolder.LAYOUT,
            parent,
            false)
        return geofenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: geofenceViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}

class geofenceViewHolder(val binding : ListOfActiveGeofenceBinding) : RecyclerView.ViewHolder(binding.root)
{
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_of_active_geofence
    }
}