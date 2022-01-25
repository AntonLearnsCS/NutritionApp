package com.example.nutritionapp.localdatatest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
import com.example.nutritionapp.menu.GeofenceReferenceData
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import java.util.LinkedHashMap

class fakeIngredientRepository() :
    IngredientDataSourceInterface {

    var ingredientServiceData: LinkedHashMap<String, IngredientDataClassDTO> = LinkedHashMap()
    var notificationServiceDataDTO: LinkedHashMap<String, RecipeNotificationClassDTO> = LinkedHashMap()

    private val observableIngredients = MutableLiveData<kotlin.Result<List<IngredientDataClassDTO>>>()


    //will get ingredient searches directly from network and not the repository

    override suspend fun getIngredients(): Result<List<IngredientDataClassDTO>> {
        wrapEspressoIdlingResource {
            //make the function main-safe by switching thread to IO thread; also we maintain structured concurrency by not declaring a new
            //coroutine scope
            Log.i("test","getIngredients fake repository called")
            return Result.Success(ArrayList(ingredientServiceData.values))
        }
    }

    override suspend fun saveNewIngredient(ingredient: IngredientDataClass) {
        wrapEspressoIdlingResource {
            ingredientServiceData[ingredient.id.toString()] = IngredientDataClassDTO(
                id = ingredient.id,
                name = ingredient.name,
                quantity = ingredient.quantity,
                image = ingredient.imageUrl,
                imageType = ingredient.imageType
            )
        }
    }

    override suspend fun update(ingredient: IngredientDataClass) {
        wrapEspressoIdlingResource {
            ingredientServiceData[ingredient.id.toString()] = IngredientDataClassDTO(
                id = ingredient.id,
                name = ingredient.name,
                quantity = ingredient.quantity,
                image = ingredient.imageUrl,
                imageType = ingredient.imageType
            )
        }
    }

    //Note: we perform the transformation in the viewModel, that way we can mark the DAO functions as suspend
    override suspend fun getIngredient(id: Int): Result<IngredientDataClassDTO> {
        wrapEspressoIdlingResource {
            (ingredientServiceData[id])?.let {
                return Result.Success(it)
            }
            return Result.Error("Could not find ingredient")
        }
    }

    override suspend fun deleteAllIngredients() {
        wrapEspressoIdlingResource {
            ingredientServiceData.clear()
        }
    }

    override suspend fun clearNotificationRecipe() {
        wrapEspressoIdlingResource {
            notificationServiceDataDTO.clear()
        }
    }

    override suspend fun getAllNotificationRecipe(): Result<List<RecipeNotificationClassDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveGeofenceReference(data: GeofenceReferenceData) {
        TODO("Not yet implemented")
    }

    override suspend fun returnGeofenceReference(key: String): Result<GeofenceReferenceData> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTaskIngredient(id: Int) {
        wrapEspressoIdlingResource {
            ingredientServiceData.remove(id.toString())
        }
    }

    override suspend fun getNotificationRecipeById(key: String): Result<RecipeNotificationClassDomain>? {
        wrapEspressoIdlingResource {
            val tempResult = notificationServiceDataDTO[key]
            return Result.Success(RecipeNotificationClassDomain(recipeName = tempResult!!.recipeName, missingIngredients = tempResult!!.missingIngredients,
                image = "", id = notificationServiceDataDTO[key]!!.id))
        }
    }

    override suspend fun saveNotificationRecipe(recipeDomain: RecipeNotificationClassDomain) {
        wrapEspressoIdlingResource {
            notificationServiceDataDTO[recipeDomain.id.toString()] = RecipeNotificationClassDTO(recipeName = recipeDomain.recipeName,
            missingIngredients = recipeDomain.missingIngredients, image = "",id = recipeDomain.id)
        }
    }
}