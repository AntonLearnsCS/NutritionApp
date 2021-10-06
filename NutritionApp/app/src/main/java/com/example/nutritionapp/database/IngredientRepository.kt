package com.example.nutritionapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.Result

class IngredientRepository (private val database: IngredientDatabase) : IngredientDataSourceInterface {

    override suspend fun getIngredients(): Result<LiveData<List<IngredientDataClass>>> {
        val resultGetIngredients = database.IngredientDatabaseDao.getAllIngredients()

            if (resultGetIngredients.value?.size != 0)
            {
                val ingredientList = ArrayList<IngredientDataClassDTO>()
                resultGetIngredients.value?.let {
                    ingredientList.addAll(it).apply { reminder ->
                        //map the reminder data from the DB to the be ready to be displayed on the UI
                        IngredientDataClass(
                           name = it.name
                        )
                    }
                })
            }
            is kotlin.Result.Success<*> -> {
                val dataList = ArrayList<ReminderDataItem>()

                remindersList.value = dataList
                println("ReminderList value: " + remindersList.value)
            }
            is kotlin.Result.Error -> {
                remindersList.value = null
                showSnackBar.value = result.message
        }
    }

    override suspend fun saveIngredient(reminder: IngredientDataClass) {

    }

    override suspend fun getIngredient(id: String): Result<LiveData<IngredientDataClass>>{
        //return Result.Success(database.IngredientDatabaseDao.getIngredientById(id))
        val resultGetIngredients = database.IngredientDatabaseDao.getIngredientById(id)
        if (resultGetIngredients.value != null)
        {
            return Result.Error("Could not find ingredient")
        }
        else
        {
            return Result.Success (Transformations.map(resultGetIngredients) { IngredientDataClass(
                name = it.name,
                quantity = it.quantity,
                id = it.id
            ) })

        }
    }

    override suspend fun deleteAllIngredients() {
        database.IngredientDatabaseDao.clear()
    }

    override suspend fun deleteTaskIngredient(id: String) {
database.IngredientDatabaseDao.deleteIngredientById(id)
    }
}