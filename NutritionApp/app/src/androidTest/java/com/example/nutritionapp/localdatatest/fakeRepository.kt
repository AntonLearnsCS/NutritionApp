package com.example.nutritionapp.localdatatest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.nutritionapp.database.IngredientDao
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClass
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.LinkedHashMap

class fakeIngredientRepository() :
    IngredientDataSourceInterface {

    var ingredientServiceData: LinkedHashMap<String, IngredientDataClassDTO> = LinkedHashMap()
    var notificationServiceData: LinkedHashMap<String, RecipeNotificationClass> = LinkedHashMap()

    private val observableIngredients =
        MutableLiveData<kotlin.Result<List<IngredientDataClassDTO>>>()


    //will get ingredient searches directly from network and not the repository

    override suspend fun getIngredients(): Result<List<IngredientDataClassDTO>> {
        wrapEspressoIdlingResource {
            //make the function main-safe by switching thread to IO thread; also we maintain structured concurrency by not declaring a new
            //coroutine scope
            Log.i("test","getIngredients fake repository called")
            return Result.Success(ArrayList(ingredientServiceData.values))
            /*return withContext(IOdispatcher)
            {
                resultGetIngredients = dao.getAllIngredients()

                if (!resultGetIngredients.isNullOrEmpty()) {
                    //https://stackoverflow.com/questions/46040027/how-to-convert-livedatalistfoo-into-livedatalistbar
                    //resultGetIngredients = Result.Success(convertedIngredients)
                    return@withContext Result.Success(resultGetIngredients!!)
                } else {
                    return@withContext Result.Error("No ingredients found")
                }
            }*/
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
            /* dao.saveIngredient(
                IngredientDataClassDTO(
                    name = ingredient.name,
                    quantity = ingredient.quantity,
                    id = ingredient.id,
                    image = ingredient.imageUrl,
                    imageType = ingredient.imageType
                )
            )*/
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
            /*    dao.update(
                IngredientDataClassDTO(
                    name = ingredient.name,
                    quantity = ingredient.quantity,
                    id = ingredient.id,
                    image = ingredient.imageUrl,
                    imageType = ingredient.imageType
                )
            )*/
        }
    }

    //Note: we perform the transformation in the viewModel, that way we can mark the DAO functions as suspend
    override suspend fun getIngredient(id: Int): Result<IngredientDataClassDTO> {
        wrapEspressoIdlingResource {
            (ingredientServiceData[id])?.let {
                return Result.Success(it)
            }
            return Result.Error("Could not find ingredient")

            /* return withContext(IOdispatcher)
                {
                    //return Result.Success(database.IngredientDatabaseDao.getIngredientById(id))
                    val resultGetIngredients = dao.getIngredientById(id)
                    if (resultGetIngredients == null) {
                        return@withContext Result.Error("Could not find ingredient")
                    } else {
                        return@withContext Result.Success(resultGetIngredients)
                    }
                }*/
        }
    }

    override suspend fun deleteAllIngredients() {
        wrapEspressoIdlingResource {
            ingredientServiceData.clear()
            /*  withContext(IOdispatcher)
            {
                dao.clearIngredientEntity()
                dao.clearRecipeEntity()
            }*/
        }
    }

    override suspend fun clearNotificationRecipe() {
        wrapEspressoIdlingResource {
            notificationServiceData.clear()
            /*withContext(IOdispatcher)
            {
                dao.clearRecipeEntity()
            }*/
        }
    }

    override suspend fun deleteTaskIngredient(id: Int) {
        wrapEspressoIdlingResource {
            ingredientServiceData.remove(id.toString())
            /*   withContext(IOdispatcher)
            {
                dao.deleteIngredientById(id)
            }*/
        }
    }

    override suspend fun getNotificationRecipeById(key: String): RecipeNotificationClass? {
        wrapEspressoIdlingResource {
            return notificationServiceData[key]
            /*return withContext(IOdispatcher)
            {
                val result = dao.getNotificationRecipeById(key)
                return@withContext result
            }*/
        }
    }

    override suspend fun saveNotificationRecipe(recipe: RecipeNotificationClass) {
        wrapEspressoIdlingResource {
            notificationServiceData[recipe.mId] = recipe
            /*   withContext(IOdispatcher)
            {
                dao.saveNotificationRecipe(recipe)
            }*/
        }
    }
}