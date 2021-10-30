package com.example.nutritionapp.database
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.NoneRecipeClass
import com.example.nutritionapp.maps.RecipeNotificationClass

interface IngredientDataSourceInterface {

    //"Result" encapsulates successful outcome with a value of type [T] or a failure with message and statusCode
    suspend fun getIngredients(): Result<List<IngredientDataClassDTO>>
    suspend fun saveNewIngredient(ingredient: IngredientDataClass)
    suspend fun update(ingredient : IngredientDataClass)
    suspend fun getIngredient(id: Int): Result<IngredientDataClassDTO>
    suspend fun deleteAllIngredients()
    suspend fun deleteTaskIngredient(id: Int)
    suspend fun getNoneRecipeById(key : String) : NoneRecipeClass?
    suspend fun saveNoneRecipe (nonRecipe : NoneRecipeClass)
    suspend fun getNotificationRecipeById (key: String) : RecipeNotificationClass?
    suspend fun saveNotificationRecipe (recipe : RecipeNotificationClass)
}