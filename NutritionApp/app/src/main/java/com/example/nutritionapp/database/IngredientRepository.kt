package com.example.nutritionapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.nutritionapp.Result
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IngredientRepository(private val database: IngredientDatabase,
                           private val IOdispatcher: CoroutineDispatcher = Dispatchers.IO) : IngredientDataSourceInterface {
    //will get ingredient searches directly from network and not the repository

    override suspend fun getIngredients(): Result<LiveData<List<IngredientDataClass>>> {
        //make the function main-safe by switching thread to IO thread; also we maintain structured concurrency by not declaring a new
        //coroutine scope
        val resultGetIngredients: LiveData<List<IngredientDataClassDTO>>? =
            null
        return withContext(IOdispatcher)
        {
            // = database.IngredientDatabaseDao.getAllIngredients()
            val convertedIngredients: LiveData<List<IngredientDataClass>>

            if (!resultGetIngredients?.value.isNullOrEmpty()) {
                //https://stackoverflow.com/questions/46040027/how-to-convert-livedatalistfoo-into-livedatalistbar
                convertedIngredients = Transformations.map(
                    resultGetIngredients!!
                ) { resultList ->
                    val tempList: MutableList<IngredientDataClass> = ArrayList()
                    for (item in resultList) {
                        tempList.add(
                            IngredientDataClass(
                                name = item.name,
                                quantity = item.quantity,
                                id = item.id,
                                imageUrl = item.image,
                                imageType = item.imageType
                            )
                        )
                    }
                    tempList //by restating tempList here we are saying that convertedIngredients equals tempList and
                    //since we are in Transformations.map, we are wrapping the resulting "tempList" in a LiveData<>
                }
            //resultGetIngredients = Result.Success(convertedIngredients)
            return@withContext Result.Success(convertedIngredients)
            } else {
                return@withContext Result.Error("No ingredients found")
            }
        }
    }

    override suspend fun saveNewIngredient(ingredient: IngredientDataClass) {
        database.IngredientDatabaseDao.insert(IngredientDataClassDTO(
            name = ingredient.name,quantity = ingredient.quantity,id = ingredient.id,image = ingredient.imageUrl,
            imageType = ingredient.imageType))
    }

    override suspend fun update(ingredient: IngredientDataClass) {
        database.IngredientDatabaseDao.update(IngredientDataClassDTO(
            name = ingredient.name,quantity = ingredient.quantity,id = ingredient.id,image = ingredient.imageUrl,
            imageType = ingredient.imageType))
    }

    override suspend fun getIngredient(id: Double): Result<LiveData<IngredientDataClass>>{
        return withContext(IOdispatcher)
        {
            //return Result.Success(database.IngredientDatabaseDao.getIngredientById(id))
            val resultGetIngredients = database.IngredientDatabaseDao.getIngredientById(id)
            if (resultGetIngredients.value != null) {
                return@withContext Result.Error("Could not find ingredient")
            } else {
                return@withContext Result.Success(Transformations.map(resultGetIngredients) {
                    IngredientDataClass(
                        name = it.name,
                        quantity = it.quantity,
                        id = it.id,
                        imageUrl = it.image,
                        imageType = it.imageType
                    )
                })
            }
        }
    }

    override suspend fun deleteAllIngredients() {
        withContext(IOdispatcher)
        {
            database.IngredientDatabaseDao.clear()
        }
    }

    override suspend fun deleteTaskIngredient(id: Double) {
        withContext(IOdispatcher)
        {
            database.IngredientDatabaseDao.deleteIngredientById(id)
        }
    }
}