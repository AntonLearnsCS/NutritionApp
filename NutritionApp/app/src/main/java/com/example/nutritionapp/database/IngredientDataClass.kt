package com.example.nutritionapp.database

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

data class IngredientDataClass(
    val id: Int,
    var name : String,
    var quantity : Int = 1,
    val imageUrl : String,
    val imageType : String
) /*: Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(quantity)
        parcel.writeString(imageUrl)
        parcel.writeString(imageType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IngredientDataClass> {
        override fun createFromParcel(parcel: Parcel): IngredientDataClass {
            return IngredientDataClass(parcel)
        }

        override fun newArray(size: Int): Array<IngredientDataClass?> {
            return arrayOfNulls(size)
        }
    }
}*/
//val id : Double, val title : String, val image : String, val imageType : String
