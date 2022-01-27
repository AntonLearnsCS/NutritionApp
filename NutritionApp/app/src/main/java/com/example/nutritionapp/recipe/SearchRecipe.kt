package com.example.nutritionapp.recipe

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.RecipeLayoutBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.recipe.intolerancespinnerclasses.IntoleranceAdapter
import com.google.android.material.chip.Chip
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class SearchRecipe : Fragment() {
private lateinit var binding : RecipeLayoutBinding
//viewModel is being initialized before the detectFoodText is done running in the background
 //val viewModel by lazy { ViewModelProvider(this).get(IngredientViewModel::class.java)}



val viewModel by sharedViewModel<IngredientViewModel>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        //Q: How to inflate layout object in onViewCreated?
        //val args = SearchRecipeArgs.fromBundle(requireArguments()).ListOfIngredients//arguments?.getString("ListOfIngredients")
        //Log.i("test","args: $args")

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.recipe_layout, container,false)

        binding.viewModel = viewModel

        val intoleranceAdapter = IntoleranceAdapter(requireContext(),R.layout.intolerance_option_item, R.id.intolerance_name, viewModel.arrayOfIntolerance)

        binding.searchRecipeButton.setOnClickListener {

            viewModel.selectedIntolerance = intoleranceAdapter.selectedIntoleranceList.joinToString()
                .replace("[","").replace("]","")

            Timber.i("searchRecipeButton clicked")
            viewModel.findRecipeByIngredients()
        }

        val adapter = recipeAdapter(recipeAdapter.RecipeIngredientListener { recipe ->
            Timber.i("Recipe selected name: " + recipe.title)
            viewModel.setNavigateToRecipe(recipe)
            viewModel.setNavigateToRecipeFlag(true)

            viewModel.getRecipeInstructions()
            //Placing the navigation step outside of the flag results in the destination fragment's viewModel
            // not having the updated value.
            //Resolved: https://knowledge.udacity.com/questions/737289
            Timber.i("food in text size: " + viewModel.foodInText.size)
            //Once getRecipeInstructions() is complete it will set mFlag = true so that navigation happens only after the liveData in getRecipeInstructions is updated
            viewModel.mFlag.observe(viewLifecycleOwner, Observer {
                viewModel.setNavigateToRecipeFlag(false)

                if (it) {
                    Log.i("test","arg: ${viewModel.navigateToRecipe.value}")
                    findNavController().navigate(
                        SearchRecipeDirections.actionSearchRecipeToRecipeDetail(
                            viewModel.navigateToRecipe.value!!
                        )
                    )
                    viewModel.mFlag.value = false
                }
            })
        })

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.recipeRecyclerView.adapter = adapter

        //for creating the chips from the selected ingredients
        if (!viewModel.foodInText.isEmpty()) {
            for (i in viewModel.foodInText) {
                val mChip = Chip(binding.chipGroupView.context)
                mChip.text = i
                //mChip.explicitStyle = R.color.chip_color
                    mChip.isClickable = true
                    mChip.isCheckable = true
                    mChip.isCloseIconVisible = true

                mChip.setOnCloseIconClickListener {
                    //redundant line directly below since context is chipViewGroup?
                    binding.chipGroupView.removeView(it)
                    viewModel.foodInText.remove(mChip.text)
                }
                binding.chipGroupView.addView(mChip)
            }
        }
        //adds new chips
        binding.addChipButton.setOnClickListener {

            if (viewModel.ingredientToBeAddedAsChip.value.isNullOrEmpty() == false) {

                    val mChip = Chip(binding.chipGroupView.context)
                    mChip.text = viewModel.ingredientToBeAddedAsChip.value
                    //mChip.explicitStyle = R.color.chip_color
                    //mChip.isClickable = true
                    mChip.isCheckable = true
                    mChip.isCloseIconVisible = true
                    val addIngredient = viewModel.ingredientToBeAddedAsChip.value
                    viewModel.foodInText.add(addIngredient!!)
                    mChip.setOnCloseIconClickListener {
                        binding.chipGroupView.removeView(it)
                        viewModel.foodInText.remove(mChip.text)
                    }
                    binding.chipGroupView.addView(mChip)
                }
            Log.i("test","foodInText: ${viewModel.foodInText}")
        }

        binding.recipeOptionsSpinner.adapter = ArrayAdapter(this.requireContext(),R.layout.support_simple_spinner_dropdown_item,viewModel.arrayOfRecipeFilterOptions)

        //implementation from: https://www.youtube.com/watch?v=D5l7MNlqA3M&ab_channel=CodeAndroid
        binding.recipeOptionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.selectedFilter.value = viewModel.arrayOfRecipeFilterOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.intoleranceOptionsSpiner.adapter = intoleranceAdapter//ArrayAdapter(this.requireContext(),R.layout.support_simple_spinner_dropdown_item, R.id.intolerance_name, viewModel.arrayOfIntolerance)

        binding.intoleranceOptionsSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        viewModel.listOfRecipesLiveData?.observe(viewLifecycleOwner, Observer{
            adapter.submitList(it)
        })

        viewModel.navigateToRecipeFlag.observe(viewLifecycleOwner, Observer {
            if (it) {

            }
        }
        )

        //source: https://stackoverflow.com/questions/55074497/how-to-add-onbackpressedcallback-to-fragment
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.i("test","SearchRecipe on back pressed")
                    viewModel.searchRecipeEditTextFlag.value = true
                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setMissingIngredientsNull()
        viewModel.selectedIntolerance = ""
        viewModel.selectedFilter.value = ""

        for (item in viewModel.arrayOfIntolerance)
        {
            item.isChecked = false
        }
    }
}
