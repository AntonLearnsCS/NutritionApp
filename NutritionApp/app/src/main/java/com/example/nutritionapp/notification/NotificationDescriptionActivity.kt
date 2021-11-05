package com.example.nutritionapp.notification

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.NotificationDescriptionActivityBinding
import com.example.nutritionapp.maps.RecipeNotificationClass

class NotificationDescriptionActivity : AppCompatActivity() {
    private lateinit var binding : NotificationDescriptionActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_description_activity)
        binding = DataBindingUtil.setContentView(this, R.layout.notification_description_activity)

        val bundleItem = intent.getSerializableExtra("EXTRA_recipeNotification") as RecipeNotificationClass//intent.getSerializableExtra("RecipeNotificationClass") as RecipeNotificationClass

        binding.recipeNotification = bundleItem

        binding.lifecycleOwner = this
    }
}