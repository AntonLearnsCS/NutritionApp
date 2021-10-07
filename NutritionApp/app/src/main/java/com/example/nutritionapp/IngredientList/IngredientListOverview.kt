package com.example.nutritionapp.IngredientList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.IngredientListRecyclerviewBinding
import org.koin.android.ext.android.inject
import org.koin.core.Koin

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
        viewModel.listOfIngredients?.observe(viewLifecycleOwner, Observer {
            ingredientAdapter.listIngredients = it
        })

    return binding.root
    }
}