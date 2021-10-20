package com.example.nutritionapp.ingredientlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.databinding.IngredientListRecyclerviewBinding
import com.example.nutritionapp.recipe.ListSelectedIngredients
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject

class IngredientListOverview : Fragment ()
{

    private val localIngredientAdapter = com.example.nutritionapp.ingredientlist.localIngredientAdapter(
        com.example.nutritionapp.ingredientlist.localIngredientAdapter.LocalIngredientListener{
            ingredientItem -> viewModel.setNavigateToDetail(ingredientItem) })

    private val networkIngredientAdapter = networkIngredientAdapter(com.example.nutritionapp.ingredientlist.networkIngredientAdapter
        .NetworkIngredientListener { ingredientItem ->
            viewModel.setNavigateToDetail(ingredientItem) })
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

        binding.recyclerViewLocal.adapter = localIngredientAdapter
        binding.recyclerViewNetwork.adapter = networkIngredientAdapter

        //Note: Layout manager must be specified for the RecyclerView to be implemented
        binding.recyclerViewNetwork.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewLocal.layoutManager = LinearLayoutManager(context)

        binding.ingredientViewModel = viewModel

        //Load from local Database
        wrapEspressoIdlingResource {
            viewModel.getLocalIngredientList()
        }

        //TODO: Not observing properly or listOfSavedIngredients is not being passed in correctly?
        viewModel.listOfSavedIngredients.observe(viewLifecycleOwner, Observer {
            localIngredientAdapter.submitList(it)
        })

        viewModel.mutableLiveDataList.observe(viewLifecycleOwner, Observer {
            networkIngredientAdapter.submitList(it)
        })

        binding.searchIngredientFAB.setOnClickListener {
            //TODO: Clear the previous search results in recyclerView
            //networkIngredientAdapter.currentList.clear()

           viewModel.loadIngredientListByNetwork()}

        binding.searchRecipe.setOnClickListener {
            val serialArg = ListSelectedIngredients(localIngredientAdapter.mList)
            Log.i("test","arg: ${serialArg.mList?.size}")
            findNavController().navigate(IngredientListOverviewDirections.actionIngredientListOverviewToSearchRecipe(serialArg))
        }

        Log.i("test","IngredientListcalled")
        if (viewModel.navigatorFlag.value == true) {
            viewModel.setNavigateToDetailNull()
            viewModel.navigatorFlag.value = false
        }
            viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
                if (viewModel.navigateToDetail.value != null) {
                    viewModel.selectedIngredient.value = it
                    findNavController().navigate(IngredientListOverviewDirections.actionIngredientListOverviewToIngredientDetail())
                }
            })

    return binding.root
    }
    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        wrapEspressoIdlingResource {  viewModel.getLocalIngredientList()}
        //viewModel.setNavigateToDetailNull()
    }
}