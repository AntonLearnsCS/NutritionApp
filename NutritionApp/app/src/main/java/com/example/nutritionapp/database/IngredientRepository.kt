package com.example.nutritionapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClass
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IngredientRepository(private val database: IngredientDatabase,
                           private val IOdispatcher: CoroutineDispatcher = Dispatchers.IO) : IngredientDataSourceInterface {
    //will get ingredient searches directly from network and not the repository

    override suspend fun getIngredients(): Result<List<IngredientDataClassDTO>> {
        wrapEspressoIdlingResource {
        //make the function main-safe by switching thread to IO thread; also we maintain structured concurrency by not declaring a new
        //coroutine scope
        var resultGetIngredients: List<IngredientDataClassDTO>? = null

        return withContext(IOdispatcher)
        {
            resultGetIngredients = database.IngredientDatabaseDao.getAllIngredients()

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
            database.IngredientDatabaseDao.saveIngredient(
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
            database.IngredientDatabaseDao.update(
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
                    val resultGetIngredients = database.IngredientDatabaseDao.getIngredientById(id)
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
                database.IngredientDatabaseDao.clear()
            }
        }
    }

    override suspend fun deleteTaskIngredient(id: Int) {
        wrapEspressoIdlingResource {
            withContext(IOdispatcher)
            {
                database.IngredientDatabaseDao.deleteIngredientById(id)
            }
        }
    }

    override suspend fun getNotificationRecipeById(key: String): RecipeNotificationClass? {
        wrapEspressoIdlingResource {
            return withContext(IOdispatcher)
            {
                val result = database.RecipeIngredient.getNotificationRecipeById(key)
                return@withContext result
            }
        }
    }

    override suspend fun saveNotificationRecipe(recipe: RecipeNotificationClass) {
wrapEspressoIdlingResource {
    withContext(IOdispatcher)
    {
        database.RecipeIngredient.saveNotificationRecipe(recipe)
    } }}
}