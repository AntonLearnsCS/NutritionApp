package com.example.nutritionapp.database
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
import com.example.nutritionapp.menu.GeofenceReferenceData
import com.example.nutritionapp.recipe.RecipeIngredientResult

interface IngredientDataSourceInterface {

    //"Result" encapsulates successful outcome with a value of type [T] or a failure with message and statusCode
    suspend fun getIngredients(): Result<List<IngredientDataClassDTO>>
    suspend fun saveNewIngredient(ingredient: IngredientDataClass)
    suspend fun update(ingredient : IngredientDataClass)
    suspend fun getIngredient(id: Int): Result<IngredientDataClassDTO>
    suspend fun deleteAllIngredients()
    suspend fun deleteTaskIngredient(id: Int)

    suspend fun getNotificationRecipeById (key: String) : Result<RecipeNotificationClassDomain>?
    suspend fun saveNotificationRecipe (recipeDomain : RecipeNotificationClassDomain)
    suspend fun clearNotificationRecipe()
    suspend fun getAllNotificationRecipe() : Result<List<RecipeNotificationClassDTO>>

    suspend fun saveGeofenceReference( data : GeofenceReferenceData)
    suspend fun returnGeofenceReference(key : String) : Result<GeofenceReferenceData>

    suspend fun saveRecipeIngredientResult( data : RecipeIngredientResult)
    suspend fun getRecipeIngredientResultList() : Result<List<RecipeIngredientResult>>
}