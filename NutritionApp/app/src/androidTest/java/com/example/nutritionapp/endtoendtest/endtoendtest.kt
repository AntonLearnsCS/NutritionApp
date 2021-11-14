package com.example.nutritionapp.endtoendtest

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4

import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nutritionapp.App
import com.example.nutritionapp.DataBindingIdlingResource
//import com.example.nutritionapp.DataBindingIdlingResource
import com.example.nutritionapp.R
import com.example.nutritionapp.authentication.AuthenticationActivity
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.ingredientlist.IngredientListActivity
import com.example.nutritionapp.ingredientlist.IngredientListOverview
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.ingredientlist.localIngredientAdapter
import com.example.nutritionapp.monitorActivity
import com.example.nutritionapp.network.mNutritionApi
//import com.example.nutritionapp.monitorActivity
import com.example.nutritionapp.util.EspressoIdleResource
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.junit.After
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
    private lateinit var repository: IngredientDataSourceInterface
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private val nutritionApi = mNutritionApi()
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun init() = runBlocking{
        //this may cause UI to hang
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            IngredientDatabase::class.java
        ).build()
/*
Q: Could be causing UI to hang?
.setTransactionExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .setQueryExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .allowMainThreadQueries()
 */
        database.clearAllTables()
        //database.IngredientDatabaseDao.clear()
        repository = ((ApplicationProvider.getApplicationContext()) as App).taskRepository
        repository.saveNewIngredient(IngredientDataClass(1, "DescriptionM", 2, "url", "jpeg"))
        repository.saveNewIngredient(IngredientDataClass(2, "DescriptionQ", 3, "url", "png"))

        viewModel = IngredientViewModel(ApplicationProvider.getApplicationContext(),repository, nutritionApi)
    }

    @After
    fun close() = runBlocking {
        database.IngredientDatabaseDao.clearIngredientEntity()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdleResource.countingIdlingResource)
        //we register an IdlingResource class seperately for databinding since Espresso uses a different mechanism for
        //databinding (Choreographer class)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdleResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun endToEndTest(): Unit = runBlocking{
        //activityScenario is part of AndroidX testing
       /* val testIngredient = IngredientDataClassDTO(9,"name",2,"http/someUrl","JPEG")
        database.IngredientDatabaseDao.saveIngredient(testIngredient)

        //TODO: Returns correctly when called directly from database
        val returnedItemDatabase = database.IngredientDatabaseDao.getAllIngredients()
        assertThat(returnedItemDatabase?.size, `is`(1))*/

        /*
         val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)

        //So espresso knows which activity to monitor
         */

        val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).perform(click())


        val listActivityScenario = ActivityScenario.launch(IngredientListActivity::class.java)

        //You must monitor the very first activity if it contains databinding in order to then monitor subsequent activities
        dataBindingIdlingResource.monitorActivity(listActivityScenario)
        //onView(withId(R.menu.overflow_menu)).perform(click())
        //Note: It seems that you can only test action bar items like a menu if you launch the activity,
        // especially if the menu item is found in the fragment contained in the activity.
        Thread.sleep(1000)
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        //Sleep b/c openActionBarOverflowOrOptionsMenu takes a while to take effect
        Thread.sleep(3000)
        onView(withText(R.string.google_maps)).check(matches(isDisplayed()))

        onView(withText(R.string.about)).check(matches(isDisplayed()))

        Espresso.pressBack()

        onView(withId(R.id.searchIngredient)).perform(replaceText("Apple"), closeSoftKeyboard())
        onView(withId(R.id.shopping_cart)).check(matches(isDisplayed()))

        onView(withId(R.id.searchIngredientButton)).perform(click())
        onView(withId(R.id.progress_circular)).check(matches(isDisplayed()))
        delay(2000)

        //TODO: recycler_view_local not recognized despite being visible on screen
        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))



        onView(withId(R.id.recycler_view_local)).perform(
            RecyclerViewActions.actionOnItemAtPosition<localIngredientAdapter.ViewHolder>
                (0, ViewActions.click())
        )
        delay(2000)
        //TODO: shopping_cart still in view during end to end test
        onView(withId(R.id.shopping_cart)).check(matches(not(isDisplayed())))

        onView(withId(R.id.recycler_view_network)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view_local))
            .check(matches(hasDescendant(withText("DescriptionQ"))))
       /* onView(withId(R.id.recycler_view_network)).perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>
            (ViewMatchers.hasDescendant(ViewMatchers.withText("Snapple Apple, 16 fl oz glass bottles, 12 pack")), click()))*/

        onView(withId(R.id.addIngredientFAB)).perform(click())
        //verify(mockNavController).navigate(IngredientListOverviewDirections.actionIngredientListOverviewToIngredientDetail())

        onView(withId(R.id.recycler_view_network)).check(matches(isDisplayed()))

        viewModel.getLocalIngredientList()

        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))

        //TODO: Was receiving: "CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch views"

        //TODO: Why does "listOfSavedIngredients" have a null value?
        assertThat(viewModel.listOfSavedIngredients?.value?.size, `is`(1))

        //onView(withId())

        //onView(withId(R.id.test)).check(matches(withText(not(containsString("NutritionApp")))))
        activityScenario.close()
        listActivityScenario.close()
    }
    var DEFAULT_WAIT_TIMEOUT = 10000L
    var DEFAULT_SLEEP_INTERVAL = 10000L
    fun waitUntilCondition(matcher: Matcher<View>, timeout: Long = DEFAULT_WAIT_TIMEOUT, condition: (View?) -> Boolean) {

        var success = false
        lateinit var exception: NoMatchingViewException
        val loopCount = timeout / DEFAULT_SLEEP_INTERVAL
        (0..loopCount).forEach {
            onView(matcher).check { view, noViewFoundException ->
                if (condition(view)) {
                    success = true
                    return@check
                } else {
                    Thread.sleep(DEFAULT_SLEEP_INTERVAL)
                    exception = noViewFoundException
                }
            }

            if (success) {
                return
            }
        }
        throw exception
    }
}