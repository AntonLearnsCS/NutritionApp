package com.example.nutritionapp.recipe

import java.io.Serializable

//Recipe page data class
data class RecipeIngredientResultWrapper (val mList : List<RecipeIngredientResult>)

data class RecipeIngredientResult (val id : Long, val title : String, val image : String, val usedIngredientCount : Int,
                                   val missedIngredientCount : Int, val likes : Int) : Serializable