package com.example.nutritionapp.recipe

//Recipe page data class
data class RecipeIngredientResultWrapper (val mList : List<RecipeIngredientResult>)

data class RecipeIngredientResult (val id : Long, val title : String, val image : String, val usedIngredientCount : Int,
                                   val missedIngredientCount : Int, val likes : Int)