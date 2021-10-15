package com.example.nutritionapp.ingredientlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.nutritionapp.R
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.databinding.IngredientListRecyclerviewBinding
import com.example.nutritionapp.network.NutritionAPI
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
         binding = DataBindingUtil.inflate(inflater, R.layout.ingredient_list_recyclerview,container,false)
        //binding.ingredientList = viewModel.listOfIngredients.value
        binding.lifecycleOwner = this

        val ingredientAdapter = IngredientAdapter()
        binding.recyclerViewLocal.adapter = ingredientAdapter
        viewModel.getLocalIngredientList()
        //updates recyclerView

        viewModel.listOfSavedIngredients?.observe(viewLifecycleOwner, Observer {
            ingredientAdapter.listIngredients = it
        })

        binding.searchIngredientFAB.setOnClickListener {
           viewModel.loadIngredientListByNetwork()
            }
        binding.test.text

    return binding.root
    }

}