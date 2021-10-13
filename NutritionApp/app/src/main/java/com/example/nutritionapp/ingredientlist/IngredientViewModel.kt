package com.example.nutritionapp.ingredientlist

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import com.example.nutritionapp.network.NutritionAPI
import com.example.nutritionapp.database.IngredientDataClass
import kotlinx.coroutines.launch
import com.example.nutritionapp.Result
import com.example.nutritionapp.database.IngredientDataSourceInterface

class IngredientViewModel (val ingredientRepository : IngredientDataSourceInterface) : ViewModel() {

    var listOfNetworkRequestedIngredients : List<IngredientDataClass>? = null //getIngredientListByNetwork()

    var listOfSavedIngredients : LiveData<List<IngredientDataClass>>? = getIngredientList()

     private val _searchFilter : MutableLiveData<String> = MutableLiveData("/food/products/search?query=Apple")
    val searchFilter : LiveData<String>
    get() = _searchFilter

    fun updateFilter(value : String)
    {
        _searchFilter.value = value
    }

    fun loadIngredientListByNetwork()
    {
        viewModelScope.launch {
            if (searchFilter.value != null) {
                val result = NutritionAPI.nutritionService.getProperties(searchFilter.value!!)
                listOfNetworkRequestedIngredients = result.map { IngredientDataClass(name = it.name,quantity = 1,id = it.id,
                imageUrl = it.imageUrl,imageType = it.imageType) }
                val networkRequestSuccess = listOfNetworkRequestedIngredients!!.size != 0

                Toast.makeText(ApplicationProvider.getApplicationContext(),"$networkRequestSuccess",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(ApplicationProvider.getApplicationContext(),"Missing search parameter",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getIngredientList() : LiveData<List<IngredientDataClass>>?
    {
        var result : LiveData<List<IngredientDataClass>>? = null
        //need DAO and repository
        viewModelScope.launch {
            val ingredientResult = ingredientRepository.getIngredients()
             when(ingredientResult)
             {
                 is Result.Success<LiveData<List<IngredientDataClass>>> ->
                 {
                     result = ingredientResult.data
                 }
                is Result.Error ->
                {
                    Log.i("test","empty repository")
                //Toast.makeText(ApplicationProvider.getApplicationContext(),"${ingredientResult.message}",Toast.LENGTH_SHORT).show()
                }
             }
        }
    return result
    }

    fun getIngredientById(id : Int) : LiveData<IngredientDataClass>?
    {
        var result : LiveData<IngredientDataClass>? = null
        viewModelScope.launch {
            val ingredientResult : Result<LiveData<IngredientDataClass>> = ingredientRepository.getIngredient(id)

            when(ingredientResult)
            {
                is Result.Success<LiveData<IngredientDataClass>> ->
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
    }

    @Override
    override fun onCleared() {
        super.onCleared()
    }
}