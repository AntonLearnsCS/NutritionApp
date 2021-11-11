package com.example.nutritionapp.database

import android.util.Log
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClass
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IngredientRepository(private val dao: IngredientDao,
                           private val IOdispatcher: CoroutineDispatcher = Dispatchers.IO) : IngredientDataSourceInterface {
    //will get ingredient searches directly from network and not the repository

    override suspend fun getIngredients(): Result<List<IngredientDataClassDTO>> {
        Log.i("test","getIngredients real repo called")
        wrapEspressoIdlingResource {
        //make the function main-safe by switching thread to IO thread; also we maintain structured concurrency by not declaring a new
        //coroutine scope
        var resultGetIngredients: List<IngredientDataClassDTO>? = null

        return withContext(IOdispatcher)
        {
            resultGetIngredients = dao.getAllIngredients()

            if (!resultGetIngredients.isNullOrEmpty()) {
                //https://stackoverflow.com/questions/46040027/how-to-convert-livedatalistfoo-into-livedatalistbar
            //resultGetIngredients = Result.Success(convertedIngredients)
            return@withContext Result.Success(resultGetIngredients!!)
            } else {
                return@withContext Result.Error("No ingredients found")
            }
        }
    }
    }

    override suspend fun saveNewIngredient(ingredient: IngredientDataClass) {
        wrapEspressoIdlingResource {
            dao.saveIngredient(
                IngredientDataClassDTO(
                    name = ingredient.name,
                    quantity = ingredient.quantity,
                    id = ingredient.id,
                    image = ingredient.imageUrl,
                    imageType = ingredient.imageType
                )
            )
        }
    }

    override suspend fun update(ingredient: IngredientDataClass) {
        wrapEspressoIdlingResource {
            dao.update(
                IngredientDataClassDTO(
                    name = ingredient.name,
                    quantity = ingredient.quantity,
                    id = ingredient.id,
                    image = ingredient.imageUrl,
                    imageType = ingredient.imageType
                )
            )
        }
    }
        //Note: we perform the transformation in the viewModel, that way we can mark the DAO functions as suspend
    override suspend fun getIngredient(id: Int): Result<IngredientDataClassDTO> {
            wrapEspressoIdlingResource {
                return withContext(IOdispatcher)
                {
                    //return Result.Success(database.IngredientDatabaseDao.getIngredientById(id))
                    val resultGetIngredients = dao.getIngredientById(id)
                    if (resultGetIngredients == null) {
                        return@withContext Result.Error("Could not find ingredient")
                    } else {
                        return@withContext Result.Success(resultGetIngredients)
                    }
                }
            }
        }

    override suspend fun deleteAllIngredients() {
        wrapEspressoIdlingResource {
            withContext(IOdispatcher)
            {
                dao.clearIngredientEntity()
                dao.clearRecipeEntity()
            }
        }
    }

    override suspend fun clearNotificationRecipe() {
        wrapEspressoIdlingResource {
            withContext(IOdispatcher)
            {
                dao.clearRecipeEntity()
            }
        }
    }

    override suspend fun deleteTaskIngredient(id: Int) {
        wrapEspressoIdlingResource {
            withContext(IOdispatcher)
            {
                dao.deleteIngredientById(id)
            }
        }
    }

    override suspend fun getNotificationRecipeById(key: String): RecipeNotificationClass? {
        wrapEspressoIdlingResource {
            return withContext(IOdispatcher)
            {
                val result = dao.getNotificationRecipeById(key)
                return@withContext result
            }
        }
    }

    override suspend fun saveNotificationRecipe(recipe: RecipeNotificationClass) {
wrapEspressoIdlingResource {
    withContext(IOdispatcher)
    {
        dao.saveNotificationRecipe(recipe)
    } }}
}