package com.example.nutritionapp.recipe

data class PostRequestResultWrapper(val annotation : List<IngredientResult>, val processedInMs : Int) {
}

data class IngredientResult(val annotation : String, val tag : String)