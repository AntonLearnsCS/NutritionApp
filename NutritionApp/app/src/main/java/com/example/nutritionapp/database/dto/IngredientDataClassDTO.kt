package com.example.nutritionapp.database.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//seperation of concern from database data and UI data
@Entity(tableName = "Ingredient_Entity")
data class IngredientDataClassDTO(
                                @PrimaryKey
                                @ColumnInfo(name = "id")
                                val id: Int,
                                @ColumnInfo(name = "name")
                               var name : String,
                                @ColumnInfo(name = "quantity")
                               var quantity : Int = 0,
                                @ColumnInfo(name = "imageUrl")
                                val image : String,
                                @ColumnInfo(name = "imageType")
                                val imageType : String){
}
//    val id : Double, val title : String, val image : String, val imageType : String