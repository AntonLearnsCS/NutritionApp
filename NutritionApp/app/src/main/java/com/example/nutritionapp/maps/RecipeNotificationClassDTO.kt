package com.example.nutritionapp.maps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity (tableName = "RecipeEntity")
data class RecipeNotificationClassDTO(@ColumnInfo(name = "recipeName")var recipeName :String,
                                      @ColumnInfo(name = "missingIngredients") var missingIngredients : String,
                                      @PrimaryKey @ColumnInfo(name = "mId") var mId: String = UUID.randomUUID().toString()) : Serializable

data class RecipeNotificationClassDomain(var recipeName :String, var missingIngredients : String,
                                         var mId: String)