package com.example.nutritionapp.recipe

import java.io.Serializable

//recipe ingredients

data class RecipeIngredientResult (val id : Long, val title : String, val image : String, val usedIngredientCount : Int,
                                   val missedIngredientCount : Int, val likes : Int) : Serializable


//recipe steps

data class RecipeInstructionsWrapper(val mList : List<RecipeInstruction>)

data class RecipeInstruction(val name : String, val steps : List<RecipeInstructionSteps>)

data class RecipeInstructionSteps(val number: Int, val step: String, val ingredients : List<ingredientsAndEquipment>,
val equipment : List<ingredientsAndEquipment>)

data class ingredientsAndEquipment(val id : Long, val name : String, val localizedName : String, val image : String)
