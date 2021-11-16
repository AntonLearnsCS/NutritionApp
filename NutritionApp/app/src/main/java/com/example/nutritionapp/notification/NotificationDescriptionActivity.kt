package com.example.nutritionapp.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.NotificationDescriptionActivityBinding
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDomain

class NotificationDescriptionActivity : AppCompatActivity() {
    private lateinit var binding : NotificationDescriptionActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_description_activity)
        binding = DataBindingUtil.setContentView(this, R.layout.notification_description_activity)

        val bundleItem = intent.getSerializableExtra("EXTRA_recipeNotification") as RecipeNotificationClassDomain//intent.getSerializableExtra("RecipeNotificationClass") as RecipeNotificationClass

        binding.recipeNotification = bundleItem

        binding.lifecycleOwner = this
    }
}