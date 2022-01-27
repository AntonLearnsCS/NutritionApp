package com.example.nutritionapp.recipe.recipedetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeDetailBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.recipe.RecipeIngredientResultDomain
import com.example.nutritionapp.recipe.RecipeIngredientResultNetwork
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RecipeDetail : Fragment() {
    private lateinit var binding : RecipeDetailBinding
    private lateinit var args : RecipeIngredientResultDomain
    //"by inject()" delegate is used to lazily inject dependencies
     val viewModel by sharedViewModel<IngredientViewModel>()

    //source: https://stackoverflow.com/questions/63597340/how-to-change-imageview-images-using-onlongclicklistener-and-save-state-using-ko
    //source: https://stackoverflow.com/questions/63597340/how-to-change-imageview-images-using-onlongclicklistener-and-save-state-using-ko
    @DrawableRes
    var imageId = R.drawable.ic_launcher_background
        set(value) {
            field = value
            binding.bookmarkImageButton.setImageResource(value) //every time value is changed, ImageView is updated
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        args  = RecipeDetailArgs.fromBundle(requireArguments()).recipe
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



        binding.bookmarkImageButton.setOnClickListener {
            if (imageId == R.drawable.ic_baseline_bookmark_border_24)
            {
                Toast.makeText(context,"bookmarked!",Toast.LENGTH_SHORT).show()
                imageId = R.drawable.ic_baseline_bookmark_24
            }
            else
            {
                imageId = R.drawable.ic_baseline_bookmark_border_24
                Toast.makeText(context,"removed from bookmarked!",Toast.LENGTH_SHORT).show()
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

    override fun onStop() {
        if (imageId == R.drawable.ic_baseline_bookmark_24)
        {
            Log.i("test","onStop saved called arg: $args")
            viewModel.saveRecipeIngredientResult(args)
            viewModel.getAllRecipeIngredientResult()

        }
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        //if user presses back button or basically does not set a reminder
        viewModel.setNavigateToRecipeNull()
    }
}