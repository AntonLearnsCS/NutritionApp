package com.example.nutritionapp.ViewModelTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import com.example.nutritionapp.IngredientList.IngredientViewModel
import com.example.nutritionapp.MockRepository
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.database.IngredientRepository
import dalvik.annotation.TestTarget
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.matches
import org.mockito.ArgumentMatchers.notNull


@ExperimentalCoroutinesApi
class IngredientListViewModelTest {

    @get: Rule
    val instanceTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init()
    {

    }

    @Test
    fun IngredientListViewModel_searchIngredient_ReturnArraySize()
    {
        //Given - view model
        val viewModel = IngredientViewModel(MockRepository)
        //When - user searches "Apple"
        runBlockingTest {
            viewModel.loadIngredientListByNetwork()
        }
        //Then - returns the number of apple results from spoonacular API
        val networkRequestSuccess = viewModel.listOfNetworkRequestedIngredients?.size != 0
        assertThat(networkRequestSuccess,`is`(true))
    }
}