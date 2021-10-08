package com.example.nutritionapp.IngredientList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.IngredientListRecyclerviewBinding
import org.koin.android.ext.android.inject

class IngredientListOverview : Fragment ()
{
    //Koin
    val viewModel : IngredientViewModel by inject()
    private lateinit var binding : IngredientListRecyclerviewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        super.onCreate(savedInstanceState)
         binding = DataBindingUtil.inflate(inflater, R.layout.ingredient_list_activity_layout,container,false)
        //binding.ingredientList = viewModel.listOfIngredients.value
        binding.lifecycleOwner = this

        val ingredientAdapter = IngredientAdapter()
        binding.recyclerView.adapter = ingredientAdapter

        //updates recyclerView
        viewModel.listOfSavedIngredients?.observe(viewLifecycleOwner, Observer {
            ingredientAdapter.listIngredients = it
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(selectedIngredient: String?): Boolean {
               viewModel.updateFilter("/food/products/search?query=$selectedIngredient")
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                //Start filtering the list as user start entering the characters; get from local database
                //adapter.filter.filter(p0)
                return false
            }
        })


    return binding.root
    }
}