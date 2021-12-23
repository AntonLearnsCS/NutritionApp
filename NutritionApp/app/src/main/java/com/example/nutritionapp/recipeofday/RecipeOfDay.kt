package com.example.nutritionapp.recipeofday

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeOfDay(@PrimaryKey val id : Int, val image : String)
