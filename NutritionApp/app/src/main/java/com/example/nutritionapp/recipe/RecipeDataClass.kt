package com.example.nutritionapp.recipe

import com.squareup.moshi.Json
import java.io.Serializable

//recipe ingredients
//Note: When the returning JSON object is an array at it's outer most layer, then you do not need a wrapper class for that array
//Instead, you result could be as such: val result = List<DataType>
data class RecipeIngredientResult (val id : Long, val title : String, val image : String, val usedIngredientCount : Int,
                                   val missedIngredientCount : Int, val likes : Int) : Serializable


//recipe steps

//data class RecipeInstructionsWrapper(val mList : List<RecipeInstruction>) //unnecessary

data class RecipeInstruction(@Json(name = "name")val name : String?, @Json(name = "steps") val steps : List<RecipeInstructionSteps>?)

data class RecipeInstructionSteps(val number: Int?, val step: String?, val ingredients : List<ingredientsAndEquipment>?,
val equipment : List<ingredientsAndEquipment>?, val Length : Length?)

data class ingredientsAndEquipment(val id : Long?, val name : String?, val localizedName : String?, val image : String?,
                                   val temperature : Temperature?)

data class Temperature(val temperature : Int?, val unitMeasurement : String?)

data class Length(val number : Int?, val unitMeasurement: String?)
