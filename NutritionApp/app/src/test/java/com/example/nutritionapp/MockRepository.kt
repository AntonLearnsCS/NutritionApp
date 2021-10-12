package com.example.nutritionapp

import androidx.lifecycle.LiveData
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface

object MockRepository : IngredientDataSourceInterface {
    override suspend fun getIngredients(): Result<LiveData<List<IngredientDataClass>>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveNewIngredient(ingredient: IngredientDataClass) {
        TODO("Not yet implemented")
    }

    override suspend fun update(ingredient: IngredientDataClass) {
        TODO("Not yet implemented")
    }

    override suspend fun getIngredient(id: Int): Result<LiveData<IngredientDataClass>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllIngredients() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTaskIngredient(id: Int) {
        TODO("Not yet implemented")
    }
}