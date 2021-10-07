package com.example.nutritionapp.IngredientList

import android.widget.Toast
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import com.example.nutritionapp.database.IngredientDao
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientRepository
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import kotlinx.coroutines.launch
import com.example.nutritionapp.Result
import com.example.nutritionapp.database.IngredientDataSourceInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

class IngredientViewModel (val ingredientRepository : IngredientDataSourceInterface) : ViewModel() {
    var listOfIngredients : LiveData<List<IngredientDataClass>>? = getIngredientList()

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
                    Toast.makeText(ApplicationProvider.getApplicationContext(),"${ingredientResult.message}",Toast.LENGTH_SHORT).show()
                }
             }
        }
    return result
    }

    fun getIngredientById(id : String) : LiveData<IngredientDataClass>?
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