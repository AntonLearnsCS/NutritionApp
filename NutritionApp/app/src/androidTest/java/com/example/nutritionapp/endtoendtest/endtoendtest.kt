package com.example.nutritionapp.endtoendtest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4

import androidx.test.filters.LargeTest
import com.example.nutritionapp.R
import com.example.nutritionapp.authentication.AuthenticationActivity
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.database.IngredientRepository
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class endtoendtest {
@get:Rule
val instanceTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel : IngredientViewModel
    private lateinit var database: IngredientDatabase
    private lateinit var repository: IngredientRepository
    @Before
    fun init() = runBlocking{
     database = IngredientDatabase.getInstance(getApplicationContext())

        database.IngredientDatabaseDao.clear()
        repository = IngredientRepository(database)
        viewModel = IngredientViewModel(repository)
    }
    @Test
    fun endToEndTest(): Unit = runBlocking {
        //activityScenario is part of AndroidX testing
        database.IngredientDatabaseDao.saveIngredient(
            IngredientDataClassDTO(1,"name",2,"http/someUrl","JPEG"))
        
        val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).perform(click())
        delay(3000)
        onView(withId(R.id.ingredient_list_constraint_layout)).check(matches(isDisplayed()))
//        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        //recyclerview did not display b/c there was no data to display so the recyclerview collapsed on itself since height/width is
        //set to wrap_content
        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))

        assertThat(viewModel.listOfSavedIngredients?.value?.size, `is`(1))

        //onView(withId(R.id.test)).check(matches(withText(not(containsString("NutritionApp")))))
       //onView(withId(R.id.recycler_view_local)).perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(ViewMatchers.hasDescendant(ViewMatchers.withText("name")), click()))
    }
}