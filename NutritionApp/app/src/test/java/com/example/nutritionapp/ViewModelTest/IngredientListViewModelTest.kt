package com.example.nutritionapp.ViewModelTest

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.nutritionapp.MainCoroutineRule
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.MockRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

//Note: Robolectric - Running tests on Android API 29 now strictly requires a Java9 runtime or newer
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@SmallTest
class IngredientListViewModelTest {

    @get: Rule
    val instanceTaskExecutorRule = InstantTaskExecutorRule()
    @get: Rule
    val mainCoroutineRule = MainCoroutineRule()
    @Before
    fun init()
    {

    }

    @Test
    fun IngredientListViewModel_searchIngredient_ReturnArraySize() = mainCoroutineRule.runBlockingTest {
        //Given - view model
        val viewModel = IngredientViewModel(MockRepository)
        //When - user searches "Apple"
            viewModel.loadIngredientListByNetwork()

        //Then - returns the number of apple results from spoonacular API, should return "8"
        val networkRequestFailed = viewModel.listOfNetworkRequestedIngredients == null
        println("Network list size: ${viewModel.listOfNetworkRequestedIngredients?.size}")
        assertThat(networkRequestFailed,`is`(false))
    }
}