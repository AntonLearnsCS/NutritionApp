package com.example.nutritionapp.recipe

import com.squareup.moshi.Json

data class PostRequestResultWrapper(@Json(name = "annotations") val annotations : List<PostIngredientResult>,
                                    @Json(name = "processedInMs") val processedInMs : Int) {
}

data class PostIngredientResult(@Json(name = "annotation") val annotation : String, @Json(name = "tag") val tag : String)