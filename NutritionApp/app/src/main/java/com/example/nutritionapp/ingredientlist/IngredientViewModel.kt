package com.example.nutritionapp.ingredientlist

import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import com.example.nutritionapp.network.NutritionAPI
import com.example.nutritionapp.database.IngredientDataClass
import kotlinx.coroutines.launch
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.databinding.IngredientListRecyclerviewBinding
import com.example.nutritionapp.network.IngredientListNetworkDataClass
import com.example.nutritionapp.network.wrapperIngredientListNetworkDataClass
import com.example.nutritionapp.util.wrapEspressoIdlingResource

class IngredientViewModel (val ingredientRepository : IngredientDataSourceInterface) : ViewModel() {

//    val binding = IngredientListRecyclerviewBinding.inflate(LayoutInflater.from(ApplicationProvider.getApplicationContext()))
    var listOfNetworkRequestedIngredients : List<IngredientDataClass>? = null //getIngredientListByNetwork()

    var listOfSavedIngredients : MutableLiveData<List<IngredientDataClass>>? = null//getLocalIngredientList()

    //two-way binding
    //no need to add "?query=" since the getIngredients() of the IngredientsApiInterface will do that
    var searchItem = MutableLiveData<String>("Apple")

    fun loadIngredientListByNetwork()
    {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                if (searchItem.value != null) {
                    try {
                        val result :wrapperIngredientListNetworkDataClass =
                            NutritionAPI.nutritionService.getIngredients(searchItem.value!!)

                        println("Total products: ${result.totalProducts}")

                        listOfNetworkRequestedIngredients = result.products.map {
                            IngredientDataClass(
                                name = it.name, quantity = 1, id = it.id,
                                imageUrl = it.imageUrl, imageType = it.imageType
                            )
                        }
                        println("Number of items: ${listOfNetworkRequestedIngredients!!.size}")

                        val networkRequestSuccess = listOfNetworkRequestedIngredients!!.isNotEmpty()

                        Toast.makeText(
                            ApplicationProvider.getApplicationContext(),
                            "$networkRequestSuccess",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    catch (e: Exception) {
                        println("Error: ${e.message}")
                    }

                } else {
                    Toast.makeText(
                        ApplicationProvider.getApplicationContext(),
                        "Missing search parameter",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun getLocalIngredientList()
    { //need DAO and repository
        wrapEspressoIdlingResource {
        viewModelScope.launch {
            val ingredientResult : Result<List<IngredientDataClassDTO>> = ingredientRepository.getIngredients()
             when(ingredientResult)
             {
                 is Result.Success<*> ->
                 {
                     val dataList = ArrayList<IngredientDataClass>()
                     dataList.addAll((ingredientResult.data as List<IngredientDataClassDTO>).map { result ->
                         //map the reminder data from the DB to the be ready to be displayed on the UI
                         IngredientDataClass(
                             id = result.id,
                             name = result.name,
                             quantity = result.quantity,
                             imageUrl = result.image,
                             imageType = result.imageType
                         )
                     })
                     listOfSavedIngredients?.value = dataList
                 }
                is Result.Error ->
                {
                    Log.i("test","empty repository")
//                Toast.makeText(ApplicationProvider.getApplicationContext(),"${ingredientResult.message}",Toast.LENGTH_SHORT).show()
                }
             }
        }
        }
    }

   /* fun getIngredientById(id : Int)
    {
        var result : LiveData<IngredientDataClass>? = null
        viewModelScope.launch {
            val ingredientResult : Result<IngredientDataClassDTO> = ingredientRepository.getIngredient(id)

            when(ingredientResult)
            {
                is Result.Success<*> ->
                {

                result = ingredientResult.data
                //result = ingredientResult.data
                }
                is Result.Error ->
                {
                    Toast.makeText(ApplicationProvider.getApplicationContext(),"${ingredientResult.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }
        return result
    }*/

    @Override
    override fun onCleared() {
        super.onCleared()
    }
}