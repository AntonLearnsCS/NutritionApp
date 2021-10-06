package com.example.nutritionapp.database

import java.io.Serializable
import java.util.*

data class IngredientDataClass(
    var name : String = "ingredient name",
    var quantity : Int = 0,
val id: String = UUID.randomUUID().toString() //generates random id
) : Serializable