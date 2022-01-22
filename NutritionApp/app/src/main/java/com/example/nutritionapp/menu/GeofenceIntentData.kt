package com.example.nutritionapp.menu

import android.app.PendingIntent
import android.content.Intent
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import java.util.*

@Entity
data class GeofenceReferenceData(
    @PrimaryKey
    val id: String, val recipeName : String, val missingIngredients : String)


