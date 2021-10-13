package com.example.nutritionapp.database
import androidx.lifecycle.LiveData
import com.example.nutritionapp.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO

interface IngredientDataSourceInterface {

    //"Result" encapsulates successful outcome with a value of type [T] or a failure with message and statusCode
    suspend fun getIngredients(): Result<List<IngredientDataClassDTO>>
    suspend fun saveNewIngredient(ingredient: IngredientDataClass)
    suspend fun update(ingredient : IngredientDataClass)
    suspend fun getIngredient(id: Int): Result<IngredientDataClassDTO>
    suspend fun deleteAllIngredients()
    suspend fun deleteTaskIngredient(id: Int)
}