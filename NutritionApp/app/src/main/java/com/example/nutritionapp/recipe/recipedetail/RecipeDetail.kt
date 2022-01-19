package com.example.nutritionapp.recipe.recipedetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeDetailBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.recipe.RecipeIngredientResult
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RecipeDetail : Fragment() {
    private lateinit var binding : RecipeDetailBinding
    //"by inject()" delegate is used to lazily inject dependencies
     val viewModel by sharedViewModel<IngredientViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val args  = RecipeDetailArgs.fromBundle(requireArguments()).recipe
        binding.recipe = args

        binding.findGroceries.setOnClickListener {
            findNavController().navigate(RecipeDetailDirections.actionRecipeDetailToMapGroceryReminder(args))
        }
        coordinateMotion()
        viewModel.missingIngredients.value.apply {
            if (!isNullOrEmpty())
            {
                for (item in this)
                {

                    val mChip = Chip(binding.recipeDetailChipGroup.context)
                    mChip.text = item
                    //mChip.explicitStyle = R.color.chip_color
                    mChip.isClickable = false
                    mChip.isCheckable = false
                    mChip.isCloseIconVisible = false

                    binding.recipeDetailChipGroup.addView(mChip)
                }
            }
        }

        return binding.root
    }

    fun coordinateMotion()
    {
        val listener = AppBarLayout.OnOffsetChangedListener { unused, verticalOffset ->
            val seekPosition = -verticalOffset / binding.appBarLayout.totalScrollRange.toFloat()
            binding.motionLayout.progress = seekPosition
        }
        binding.appBarLayout.addOnOffsetChangedListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        //if user presses back button or basically does not set a reminder
        viewModel.setNavigateToRecipeNull()
    }
}