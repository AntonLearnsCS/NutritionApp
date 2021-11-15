package com.example.nutritionapp.ingredientdetail

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
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito

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

    @MediumTest
    fun ingredientDetail_ImageAndQuantityAndTitle_Updates() {
        //given - IngredientDetail Fragment
        //when - An IngredientDataClass is saved to the viewModel
        val scenario = launchFragmentInContainer<IngredientDetail>()
        val navController = Mockito.mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //then - The saved ingredient's name, quantity and image appears in the fragment

        onView(withId(R.id.ingredient_detail_name)).check(matches(withText(viewModel.selectedIngredient.value?.name)))
        onView(withId(R.id.quantity)).check(matches(withText(viewModel.selectedIngredient.value?.quantity!!)))
        onView(withContentDescription("Image of: ${viewModel.selectedIngredient.value!!.name}")).check(matches(isDisplayed()))

        onView(withId(R.id.addIngredientFAB)).perform(click())

        verify(navCon)
    }
}