package com.example.nutritionapp.database

import java.io.Serializable
import java.util.*

data class IngredientDataClass(
    val id: Int,
    var name : String,
    var quantity : Int = 1,
    val imageUrl : String,
    val imageType : String
) : Serializable
//val id : Double, val title : String, val image : String, val imageType : String
