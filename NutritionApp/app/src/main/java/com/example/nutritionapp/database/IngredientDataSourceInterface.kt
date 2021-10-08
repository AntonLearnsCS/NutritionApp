package com.example.nutritionapp.database
import androidx.lifecycle.LiveData
import com.example.nutritionapp.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO

interface IngredientDataSourceInterface {

    //"Result" encapsulates successful outcome with a value of type [T] or a failure with message and statusCode
    suspend fun getIngredients(): Result<LiveData<List<IngredientDataClass>>>
    suspend fun saveNewIngredient(ingredient: IngredientDataClass)
    suspend fun update(ingredient : IngredientDataClass)
    suspend fun getIngredient(id: String): Result<LiveData<IngredientDataClass>>
    suspend fun deleteAllIngredients()
    suspend fun deleteTaskIngredient(id: String)
}