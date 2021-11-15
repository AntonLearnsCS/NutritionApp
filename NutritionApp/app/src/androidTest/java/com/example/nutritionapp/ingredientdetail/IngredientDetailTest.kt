package com.example.nutritionapp.ingredientdetail

import android.os.Bundle
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
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.localdatatest.fakeIngredientRepository
import com.example.nutritionapp.network.mNutritionApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify

@MediumTest
@RunWith(JUnit4::class)
class IngredientDetailTest {
    private lateinit var viewModel : IngredientViewModel
    private lateinit var ingredientDataClass : IngredientDataClass
    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init()
    {
        ingredientDataClass = IngredientDataClass(1,"name",1,"url","jpeg")
        val nutritionApiObj = mNutritionApi()
        viewModel = IngredientViewModel(ApplicationProvider.getApplicationContext(),
            fakeIngredientRepository(), nutritionApiObj)

        viewModel.selectedIngredient.value = ingredientDataClass

    }
        //Note: Use @Test instead of @MediumTest here in function declaration
    @ExperimentalCoroutinesApi
    @Test
    fun ingredientDetail_ImageAndQuantityAndTitle_Updates() : Unit = runBlockingTest{
        //given - IngredientDetail Fragment
        //when - An IngredientDataClass is saved to the viewModel
        val scenario = launchFragmentInContainer<IngredientDetail>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //then - The saved ingredient's name, quantity and image appears in the fragment
        delay(2000)
        onView(withId(R.id.increaseButton)).check(matches(isDisplayed()))
        onView(withId(R.id.quantity)).check(matches(isDisplayed()))

        onView(withId(R.id.quantity)).check(matches(withText(viewModel.selectedIngredient.value?.quantity!!)))


        onView(withId(R.id.ingredient_detail_name)).check(matches(isDisplayed()))

        onView(withId(R.id.ingredient_detail_name)).check(matches(withText(viewModel.selectedIngredient.value?.name)))
        onView(withContentDescription("Image of: ${viewModel.selectedIngredient.value!!.name}")).check(matches(isDisplayed()))

        onView(withId(R.id.addIngredientFAB)).perform(click())

        verify(navController.navigate(IngredientDetailDirections.actionIngredientDetailToIngredientListOverview()))
    }
}