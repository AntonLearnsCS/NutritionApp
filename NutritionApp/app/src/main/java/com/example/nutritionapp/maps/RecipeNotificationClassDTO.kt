package com.example.nutritionapp.maps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity (tableName = "RecipeEntity")
data class RecipeNotificationClassDTO(var recipeName :String,
                                      var missingIngredients : String,
                                      var image : String?,
                                      @PrimaryKey
                                      var id: Int) : Serializable

data class RecipeNotificationClassDomain(var recipeName :String, var missingIngredients : String,
                                         var image: String?,
                                         var id: Int) : Serializable