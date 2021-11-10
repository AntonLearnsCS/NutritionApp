/*
package com.example.nutritionapp.endtoendtest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4

import androidx.test.filters.LargeTest
import com.example.nutritionapp.DataBindingIdlingResource
import com.example.nutritionapp.R
import com.example.nutritionapp.authentication.AuthenticationActivity
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.database.IngredientRepository
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.ingredientlist.IngredientListActivity
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.monitorActivity
//import com.example.nutritionapp.monitorActivity
import com.example.nutritionapp.util.EspressoIdleResource
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.util.succeeded
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class endtoendtest {

@get:Rule
val instanceTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel : IngredientViewModel
    private lateinit var database: IngredientDatabase
    private lateinit var repository: IngredientRepository
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun init() = runBlocking{
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            IngredientDatabase::class.java
        ).setTransactionExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .setQueryExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .allowMainThreadQueries().build()

        database.clearAllTables()
        //database.IngredientDatabaseDao.clear()
        //TODO: Create service locator to get repository
        repository = IngredientRepository(database,Dispatchers.Main)
        viewModel = IngredientViewModel(ApplicationProvider.getApplicationContext(), repository)
    }

    @After
    fun close() = runBlocking {
        database.IngredientDatabaseDao.clear()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdleResource.countingIdlingResource)
        //we register an IdlingResource class seperately for databinding since Espresso uses a different mechanism for
        //databinding (Choreographer class)
        //IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdleResource.countingIdlingResource)
        //IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @ExperimentalCoroutinesApi
    @LargeTest
    fun endToEndTest(): Unit = mainCoroutineRule.runBlockingTest {
        //activityScenario is part of AndroidX testing
        val testIngredient = IngredientDataClassDTO(9,"name",2,"http/someUrl","JPEG")
        repository.saveNewIngredient(IngredientDataClass(id = testIngredient.id,name = testIngredient.name,quantity = testIngredient.quantity,
        imageUrl = testIngredient.image, imageType = testIngredient.imageType))

        //TODO: Returns correctly when called directly from database

        val returnedTestIngredient = repository.getIngredient(testIngredient.id)

        when (returnedTestIngredient) {
            is Result.Success<*> -> {
                val result = returnedTestIngredient.data as IngredientDataClassDTO
                assertThat(result.name, `is`(testIngredient.name))
            }
        }



        /*
         val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)

        //So espresso knows which activity to monitor
        dataBindingIdlingResource.monitorActivity(activityScenario)
         */

        val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).perform(click())

        //TODO: Idling resource only works for activities(?), how can I make espresso wait in between activities? There seems to be down time after user clicks "Login"
        delay(3000)
        //onView(withId(R.id.ingredient_list_constraint_layout)).check(matches(isDisplayed()))
//        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        //recyclerview did not display b/c there was no data to display so the recyclerview collapsed on itself since height/width is
        //set to wrap_content
        val listActivityScenario = ActivityScenario.launch(IngredientListActivity::class.java)
        //dataBindingIdlingResource.monitorActivity(listActivityScenario)

        viewModel.getLocalIngredientList()
        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))

        //TODO: Why does "listOfSavedIngredients" have a null value?
        assertThat(viewModel.listOfSavedIngredients.value?.size, not(1))

        //onView(withId(R.id.test)).check(matches(withText(not(containsString("NutritionApp")))))
       //onView(withId(R.id.recycler_view_local)).perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(ViewMatchers.hasDescendant(ViewMatchers.withText("name")), click()))
    activityScenario.close()
        listActivityScenario.close()
    }
}
* */