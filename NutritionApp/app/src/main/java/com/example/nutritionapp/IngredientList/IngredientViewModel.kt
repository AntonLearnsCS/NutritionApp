package com.example.nutritionapp.IngredientList

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.nutritionapp.database.IngredientDao
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.dto.IngredientDataClassDTO

class IngredientViewModel (val dao : IngredientDao) : ViewModel() {
    var listOfIngredients : LiveData<List<IngredientDataClass>> = getIngredientList().value.apply {

    }

    fun getIngredientList() : LiveData<List<IngredientDataClassDTO>>
    {
        //need DAO and repository
        Transformations.map(dao.getAllIngredients()) {
            //var hello : Int = database.videoDao.getVideos()

        //it.asDomainModel()
        }
        return dao.getAllIngredients()
    }
    fun getIngredientById(id : String) : LiveData<IngredientDataClass>
    {
        val value = Transformations.map( dao.getIngredientById(id)
        )
        {
             IngredientDataClass(
                name = it.name,
                quantity = it.quantity,
                id = it.id
            )
        }
       return value
    }

    @Override
    override fun onCleared() {
        super.onCleared()
    }
}