package com.example.nutritionapp.maps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

data class NoneRecipeClass(var mString : String, var id: String = UUID.randomUUID().toString()) : Serializable

@Entity (tableName = "RecipeEntity")
data class RecipeNotificationClass(  @ColumnInfo(name = "recipeName")var recipeName :String,
                                   @ColumnInfo(name = "missingIngredients") var missingIngredients : String,
                                     @PrimaryKey @ColumnInfo(name = "mId") var mId: String = UUID.randomUUID().toString()) : Serializable
