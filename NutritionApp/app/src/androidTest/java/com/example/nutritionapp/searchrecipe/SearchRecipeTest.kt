package com.example.nutritionapp.searchrecipe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.example.nutritionapp.R
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.localdatatest.fakeIngredientRepository
import com.example.nutritionapp.network.mNutritionApi
import com.example.nutritionapp.recipe.RecipeIngredientResult
import com.example.nutritionapp.recipe.SearchRecipe
import com.example.nutritionapp.recipe.SearchRecipeDirections
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.verify
import org.mockito.Mockito.mock

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
        val test = listOf<String>("hello","goodbye")
    }

    @Test
    fun searchRecipe_UI_Displays() : Unit = runBlocking{
        val mList = listOf("Apple","Carrot","Blueberry")
        //given - a SearchRecipe fragment and a Recipe
        val recipeExample = RecipeIngredientResult("1","Title","imageUrl","JPEG")
        val scenario = launchFragmentInContainer<SearchRecipe>()

        val navController = mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        //when - detectFoodInText() is called
        viewModel.detectFoodInText(mList)
        delay(2000)
        //then - editText should contain items in mList
       // onView(withText("Apple")).check(matches(isDisplayed()))
        onView(withId(R.id.search_recipe_button)).perform(click())

        verify(navController).navigate(SearchRecipeDirections.actionSearchRecipeToRecipeDetail(recipeExample))
    }

}