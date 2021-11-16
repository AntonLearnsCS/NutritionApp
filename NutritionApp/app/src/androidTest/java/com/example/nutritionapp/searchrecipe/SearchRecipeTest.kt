package com.example.nutritionapp.searchrecipe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.example.nutritionapp.R
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.localdatatest.fakeIngredientRepository
import com.example.nutritionapp.network.mNutritionApi
import com.example.nutritionapp.recipe.RecipeIngredientResult
import com.example.nutritionapp.recipe.SearchRecipe
import com.example.nutritionapp.recipe.SearchRecipeDirections
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.verify


@MediumTest
@RunWith(JUnit4::class)
class SearchRecipeTest {
    val nutritionApi = mNutritionApi()
    private val viewModel = IngredientViewModel(ApplicationProvider.getApplicationContext(),fakeIngredientRepository(),nutritionApi)

    @get: Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun init()
    {
        viewModel
    }

    @Test
    fun searchRecipe_UI_Displays()
    {
        val mList = listOf("Apple","Carrot","Blueberry")
        //given - a SearchRecipe fragment and a Recipe
        val recipeExample = RecipeIngredientResult(1,"Title","imageUrl",2,3,4)
        val scenario = launchFragmentInContainer<SearchRecipe>()

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        //when - detectFoodInText() is called
        viewModel.detectFoodInText(mList)

        //then - editText should contain items in mList
        onView(withId(R.id.ingredient_list))

        verify(navController.navigate(SearchRecipeDirections.actionSearchRecipeToRecipeDetail(recipeExample)))
    }

}