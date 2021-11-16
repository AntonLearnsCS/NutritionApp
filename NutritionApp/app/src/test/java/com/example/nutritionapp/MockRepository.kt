package com.example.nutritionapp

import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
import com.example.nutritionapp.util.Result


object MockRepository : IngredientDataSourceInterface {

    private var INGREDIENT_SERVICE_DATA = LinkedHashMap<String, IngredientDataClassDTO>()

    override suspend fun getIngredients(): Result<List<IngredientDataClassDTO>> {
        return Result.Success((INGREDIENT_SERVICE_DATA.values).toList())

    }

    override suspend fun saveNewIngredient(ingredient: IngredientDataClass) {
        INGREDIENT_SERVICE_DATA[ingredient.id.toString()] = IngredientDataClassDTO(id = ingredient.id,name = ingredient.name,
            quantity = ingredient.quantity,image = ingredient.imageUrl,imageType = ingredient.imageType)
    }

    override suspend fun update(ingredient: IngredientDataClass) {
        INGREDIENT_SERVICE_DATA[ingredient.id.toString()] = IngredientDataClassDTO(id = ingredient.id,name = ingredient.name,
        quantity = ingredient.quantity,image = ingredient.imageUrl,imageType = ingredient.imageType)
    }

    override suspend fun getIngredient(id: Int): Result<IngredientDataClassDTO> {
        val result = INGREDIENT_SERVICE_DATA[id.toString()]

    return Result.Success(IngredientDataClassDTO(
        id = result!!.id,name = result.name,quantity = result.quantity,image = result.image, imageType = result.imageType))
    }

    override suspend fun deleteAllIngredients() {
        INGREDIENT_SERVICE_DATA.clear()
    }

    override suspend fun deleteTaskIngredient(id: Int) {
        INGREDIENT_SERVICE_DATA.remove(id)
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