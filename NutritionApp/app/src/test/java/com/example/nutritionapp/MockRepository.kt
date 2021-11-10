package com.example.nutritionapp

import androidx.lifecycle.LiveData
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClass
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

    override suspend fun getNotificationRecipeById(key: String): RecipeNotificationClass? {
        TODO("Not yet implemented")
    }

    override suspend fun saveNotificationRecipe(recipe: RecipeNotificationClass) {
        TODO("Not yet implemented")
    }
}