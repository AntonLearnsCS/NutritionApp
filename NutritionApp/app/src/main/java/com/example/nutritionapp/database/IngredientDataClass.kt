package com.example.nutritionapp.database

import java.io.Serializable
import java.util.*

data class IngredientDataClass(
    var name : String,
    var quantity : Int = 0,
    val id: Int,
    val imageUrl : String,
    val imageType : String
) : Serializable
//val id : Double, val title : String, val image : String, val imageType : String
