package com.example.nutritionapp.ingredientlist

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.IngredientListRecyclerviewBinding
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class IngredientListOverview : Fragment() {


    private val localIngredientAdapter =
        com.example.nutritionapp.ingredientlist.localIngredientAdapter(
            com.example.nutritionapp.ingredientlist.localIngredientAdapter.LocalIngredientListener { ingredientItem ->
                viewModel.setNavigateToDetail(
                    ingredientItem
                )
            })

    private val networkIngredientAdapter =
        networkIngredientAdapter(com.example.nutritionapp.ingredientlist.networkIngredientAdapter
            .NetworkIngredientListener { ingredientItem ->
                viewModel.setNavigateToDetail(ingredientItem)
            })

    //Koin
    val viewModel by sharedViewModel<IngredientViewModel>()
    private lateinit var binding: IngredientListRecyclerviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.ingredient_list_recyclerview,
            container,
            false
        )
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

        binding.searchIngredientButton.setOnClickListener {
            //TODO: Clear the previous search results in recyclerView
            //networkIngredientAdapter.currentList.clear()

            viewModel.loadIngredientListByNetwork()
        }

        binding.searchRecipe.setOnClickListener {
            val result = localIngredientAdapter.getListName()

            if (result.size == 0)
            {
                viewModel.displayToast("Select at least one ingredient")
            }
            //clear list of ingredient components before trying to detect new ingredients
            viewModel.foodInText.clear()
            //Returns list of ingredients i.e {"mushroom","flour","tomato"}
            viewModel.detectFoodInText(localIngredientAdapter.mListOfNames)
            //val foodDetected : String? = viewModel.listOfIngredientsString.value
            //val serialArg = viewModel.foodInText.toString()

            localIngredientAdapter.mList.clear()
            //wait for flag to be true, indicating that the viewModelScope coroutine is done
            viewModel.navigatorFlag.observe(viewLifecycleOwner, Observer { flag ->
                if (flag) {
                    viewModel.setNavigatorFlag(false)
                    findNavController().navigate(
                        IngredientListOverviewDirections.actionIngredientListOverviewToSearchRecipe(
                        )
                    )
                    //once you navigate away, this fragment is destroyed, which destroys this method, so no need for return
                    //return@Observer
                }
            })
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            if (viewModel.navigateToDetail.value != null) {
                viewModel.selectedIngredient.value = it
                viewModel.setNavigateToDetailNull()
                findNavController().navigate(IngredientListOverviewDirections.actionIngredientListOverviewToIngredientDetail())
            }
        })

        setHasOptionsMenu(true)

        viewModel.searchRecipeEditTextFlag.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                viewModel.setSearchRecipeEditTextClear()
                localIngredientAdapter.mListOfNames.clear()
            viewModel.searchRecipeEditTextFlag.value = false
            }
        })

        //viewModel.listOfIngredientsString.value = null
        return binding.root
    }

    //override onCreateOptionsMenu() and onOptionsItemSelected() and implement setHasOptionsMenu(true) to allow for menu; also create menu xml
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //Note: Must remove call to super to use navigation
        //super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        wrapEspressoIdlingResource { viewModel.getLocalIngredientList() }
        //viewModel.setNavigateToDetailNull()
    }
}