package com.example.nutritionapp.ViewModelTest

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.nutritionapp.MainCoroutineRule
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.MockRepository
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
import com.example.nutritionapp.network.mNutritionApi
import com.example.nutritionapp.util.Result
import junit.framework.Assert.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
//import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

import org.hamcrest.Matchers.*



//Note: Robolectric - Running tests on Android API 29 now strictly requires a Java9 runtime or newer
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@SmallTest
class IngredientListViewModelTest {
    private val nutritionApi = mNutritionApi()
    @get: Rule
    val instanceTaskExecutorRule = InstantTaskExecutorRule()
    @get: Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init()
    {

    }

    @Test
    fun IngredientListViewModel_searchIngredient_ReturnArraySize() = mainCoroutineRule.dispatcher.runBlockingTest {
        //Given - view model
        val viewModel = IngredientViewModel(ApplicationProvider.getApplicationContext(), MockRepository, nutritionApi)
        val testIngredient = IngredientDataClass(1,"Name",2,"imgUrl","jpeg")
        val recipeNotification = RecipeNotificationClassDomain("recipeName","Apple, Orange","1")

        viewModel.selectedIngredient.value = testIngredient
        //When - user saves an ingredient item and recipeNotification item
        viewModel.saveIngredientItem()
        viewModel.saveRecipeNotification(recipeNotification)

        //Then - retrieve saved items
        viewModel.getLocalIngredientList()
        val recipeNotificationResult = MockRepository.getNotificationRecipeById(recipeNotification.mId) as Result.Success

        val localIngredientList = viewModel.listOfSavedIngredients.value
        assertThat(localIngredientList?.get(0), `is`(testIngredient))
        assertThat(recipeNotification.recipeName, `is`(recipeNotificationResult.data.recipeName))
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun networkResultStateFlow_Collect() = runBlocking {
        //given a mutable stateflow
        val viewModel = IngredientViewModel(ApplicationProvider.getApplicationContext(), MockRepository, nutritionApi)
        //when stateflow is declared
        val testStateFow = viewModel.networkResultStateFlow.value
        //then initially set to null
        assertThat(testStateFow?.get(0), `is`(nullValue()))
    }
}