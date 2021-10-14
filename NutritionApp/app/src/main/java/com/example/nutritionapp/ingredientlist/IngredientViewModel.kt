package com.example.nutritionapp.ingredientlist

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import com.example.nutritionapp.network.NutritionAPI
import com.example.nutritionapp.database.IngredientDataClass
import kotlinx.coroutines.launch
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.util.wrapEspressoIdlingResource

class IngredientViewModel (val ingredientRepository : IngredientDataSourceInterface) : ViewModel() {

    var listOfNetworkRequestedIngredients : List<IngredientDataClass>? = null //getIngredientListByNetwork()

    var listOfSavedIngredients : MutableLiveData<List<IngredientDataClass>>? = null//getLocalIngredientList()

     private val _searchFilter : MutableLiveData<String> = MutableLiveData("/food/products/search?query=Apple")
    val searchFilter : LiveData<String>
    get() = _searchFilter

    fun updateFilter(value : String)
    {
        _searchFilter.value = value
    }



    fun loadIngredientListByNetwork()
    {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                if (searchFilter.value != null) {
                    val result = NutritionAPI.nutritionService.getProperties(searchFilter.value!!)
                    listOfNetworkRequestedIngredients = result.map {
                        IngredientDataClass(
                            name = it.name, quantity = 1, id = it.id,
                            imageUrl = it.imageUrl, imageType = it.imageType
                        )
                    }
                    val networkRequestSuccess = listOfNetworkRequestedIngredients!!.size != 0

                    Toast.makeText(
                        ApplicationProvider.getApplicationContext(),
                        "$networkRequestSuccess",
                        Toast.LENGTH_SHORT
                    ).show()
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