package com.example.nutritionapp.network
import com.example.nutritionapp.database.IngredientDataClass
import com.squareup.moshi.Json

//from "Search Grocery Products" endpoint
data class IngredientListNetworkDataClassContainer(val ingredientList : List<IngredientListNetworkDataClass>)

data class wrapperIngredientListNetworkDataClass(
    val type : String, val products : List<IngredientListNetworkDataClass>,
    val offset : Int, val number: Int, val totalProducts : Int,
    val processingTimeMs : Int, val expires: Long, val isStale: Boolean
)
data class IngredientListNetworkDataClass (
    val id : Int,
    @Json(name = "title")val name : String,
    @Json(name = "image") val imageUrl : String,
    val imageType : String
    )

fun IngredientListNetworkDataClassContainer.toDomainType() : List<IngredientDataClass>
{
    return ingredientList.map { IngredientDataClass(name = it.name,quantity = 1,id = it.id,imageUrl = it.imageUrl,imageType = it.imageType) }
}
