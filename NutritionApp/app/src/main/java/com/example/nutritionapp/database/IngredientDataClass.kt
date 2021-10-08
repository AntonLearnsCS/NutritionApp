package com.example.nutritionapp.database

import java.io.Serializable
import java.util.*

data class IngredientDataClass(
    var name : String = "ingredient name",
    var quantity : Int = 0,
    val id: Double,
    val imageUrl : String,
    val imageType : String
) : Serializable
//val id : Double, val title : String, val image : String, val imageType : String
