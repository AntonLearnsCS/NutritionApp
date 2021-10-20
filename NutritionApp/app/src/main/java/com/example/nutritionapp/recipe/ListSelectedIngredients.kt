package com.example.nutritionapp.recipe

import com.example.nutritionapp.database.IngredientDataClass
import java.io.Serializable

data class ListSelectedIngredients(var mList : MutableList<IngredientDataClass>?) : Serializable
