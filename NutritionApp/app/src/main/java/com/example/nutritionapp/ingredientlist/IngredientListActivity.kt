package com.example.nutritionapp.ingredientlist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.nutritionapp.R
import org.koin.android.ext.android.inject

class IngredientListActivity : AppCompatActivity() {
    val viewModel: IngredientViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ingredient_list_activity_layout)
    }

    }


