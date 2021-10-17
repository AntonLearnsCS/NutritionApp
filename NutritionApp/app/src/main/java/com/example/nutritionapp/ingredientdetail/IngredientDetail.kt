package com.example.nutritionapp.ingredientdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.databinding.IngredientDetailBinding

class IngredientDetail : Fragment() {
    private lateinit var binding: IngredientDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val arg = IngredientDetailArgs.fromBundle(requireArguments())//arguments?.getSerializable("IngredientItem") as IngredientDataClass
        binding = DataBindingUtil.inflate(inflater, R.layout.ingredient_detail,container,false)
        binding.ingredientItem = arg.IngredientItem
        binding.lifecycleOwner = this

        return binding.root
    }
}