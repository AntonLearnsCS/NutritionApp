package com.example.nutritionapp

import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
import com.example.nutritionapp.util.Result


object MockRepository : IngredientDataSourceInterface {
    override suspend fun getIngredients(): Result<List<IngredientDataClassDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveNewIngredient(ingredient: IngredientDataClass) {
        TODO("Not yet implemented")
    }

    override suspend fun update(ingredient: IngredientDataClass) {
        TODO("Not yet implemented")
    }

    override suspend fun getIngredient(id: Int): Result<IngredientDataClassDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllIngredients() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTaskIngredient(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getNotificationRecipeById(key: String): Result<RecipeNotificationClassDomain>? {
        TODO("Not yet implemented")
    }

    override suspend fun saveNotificationRecipe(recipeDomain: RecipeNotificationClassDomain) {
        TODO("Not yet implemented")
    }

    override suspend fun clearNotificationRecipe() {
        TODO("Not yet implemented")
    }
}