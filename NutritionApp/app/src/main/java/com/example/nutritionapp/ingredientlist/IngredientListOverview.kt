package com.example.nutritionapp.ingredientlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
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
         binding = DataBindingUtil.inflate(inflater, R.layout.ingredient_list_recyclerview,container,false)
        //binding.ingredientList = viewModel.listOfIngredients.value
        binding.lifecycleOwner = viewLifecycleOwner

        val localIngredientAdapter = localIngredientAdapter()
        val networkIngredientAdapter = networkIngredientAdapter()

        binding.recyclerViewLocal.adapter = localIngredientAdapter
        binding.recyclerViewNetwork.adapter = networkIngredientAdapter

        viewModel.getLocalIngredientList()
        //updates recyclerView

        val testList : MutableList<IngredientDataClass> = mutableListOf<IngredientDataClass>()
        testList.add(IngredientDataClass(1,"name",2,"url","jpeg"))
        testList.add(IngredientDataClass(3,"name3",5,"url3","jpeg3"))
        networkIngredientAdapter.listIngredients = testList

        viewModel.listOfSavedIngredients?.observe(viewLifecycleOwner, Observer {
            localIngredientAdapter.listIngredients = it
        })

        viewModel.displayListInXml?.observe(viewLifecycleOwner, Observer {
             networkIngredientAdapter.listIngredients = it
        })

        binding.searchIngredientFAB.setOnClickListener {
           viewModel.loadIngredientListByNetwork()
            }
        binding.test.text

    return binding.root
    }
}