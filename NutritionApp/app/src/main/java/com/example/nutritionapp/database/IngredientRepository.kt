package com.example.nutritionapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.Result

class IngredientRepository (private val database: IngredientDatabase) : IngredientDataSourceInterface {

    override suspend fun getIngredients(): Result<LiveData<List<IngredientDataClass>>> {
        val resultGetIngredients : LiveData<List<IngredientDataClassDTO>> = database.IngredientDatabaseDao.getAllIngredients()

            if (resultGetIngredients?.size != 0)
            {
                /* val mList = ArrayList<IngredientDataClass>()
                Transformations.map(resultGetIngredients){
                    mList.addAll(IngredientDataClass(name = it[0].name,it))
                }*/
                val dataList = List<IngredientDataClass>() as LiveData
                dataList.value.addAll((resultGetIngredients as List<IngredientDataClassDTO>).map { ingredient ->
                    //map the ingredient data from the DB to the be ready to be displayed on the UI
                    IngredientDataClass(
                        name = ingredient.name,
                        quantity = ingredient.quantity,
                        id = ingredient.id
                    )
                })

                return Result.Success(dataList)//Result.Success<data>
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