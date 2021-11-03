package com.example.nutritionapp.ingredientlist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.nutritionapp.R
import org.koin.android.ext.android.inject

class IngredientListActivity : AppCompatActivity() {
    val viewModel : IngredientViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ingredient_list_activity_layout)
    }
    //source: https://stackoverflow.com/questions/5448653/how-to-implement-onbackpressed-in-fragments
    //will clear food in text list when back button is clicked
    /*override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.searchRecipe)

        (fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
            Log.i("test","IOnBackPressed called")
            viewModel.listOfIngredientsString.value = null
            super.onBackPressed()
        }
    //super.onBackPressed()
    }*/
    }

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}