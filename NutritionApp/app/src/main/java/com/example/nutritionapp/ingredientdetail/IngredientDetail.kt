package com.example.nutritionapp.ingredientdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.databinding.IngredientDetailBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class IngredientDetail : Fragment() {
    private lateinit var binding: IngredientDetailBinding
    //share viewModel
     val viewModel by sharedViewModel<IngredientViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val arg = arguments?.let { IngredientDetailArgs.fromBundle(it) }//arguments?.getSerializable("IngredientItem") as IngredientDataClass
        binding = DataBindingUtil.inflate(inflater, R.layout.ingredient_detail, container, false)
        //binding.ingredientItem = arg.IngredientItem
        binding.lifecycleOwner = viewLifecycleOwner

        binding.decreaseButton.setOnClickListener{viewModel.decreaseQuantityCounter()}
        binding.increaseButton.setOnClickListener {viewModel.increaseQuantityCounter()}
        binding.selectedIngredientViewModel = viewModel

        binding.addIngredientFAB.setOnClickListener {
            viewModel.saveIngredientItem()
            findNavController().navigate(IngredientDetailDirections.actionIngredientDetailToIngredientListOverview()) }

        return binding.root
    }
}