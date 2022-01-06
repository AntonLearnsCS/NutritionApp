package com.example.nutritionapp.endtoendtest

//import com.example.nutritionapp.DataBindingIdlingResource

import ServiceLocator
import android.app.Activity
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nutritionapp.*
import com.example.nutritionapp.R
import com.example.nutritionapp.authentication.AuthenticationActivity
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.ingredientlist.IngredientListActivity
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.ingredientlist.localIngredientAdapter
import com.example.nutritionapp.network.mNutritionApi
import com.example.nutritionapp.util.EspressoIdleResource
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.StringBuilder


@LargeTest
@RunWith(AndroidJUnit4::class)
class endtoendtest {

@get:Rule
val instanceTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel : IngredientViewModel
    private lateinit var database: IngredientDatabase
    private lateinit var repository: IngredientDataSourceInterface
    private lateinit var mList : List<IngredientDataClass>
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private val nutritionApi = mNutritionApi()

/*
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
*/

    @ExperimentalCoroutinesApi
    @Before
    fun init() = runBlocking{
        //this may cause UI to hang
        /*database = Room.inMemoryDatabaseBuilder(getApplicationContext(), IngredientDatabase::class.java).setTransactionExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .setQueryExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .allowMainThreadQueries().build()*/
/*
Q: Could be causing UI to hang?
.setTransactionExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .setQueryExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .allowMainThreadQueries()
 */
        //Note: Will cause error b/c rootView had already been created by Dispatchers.Main and so will not be recognized by new Dispatchers.Main
        /*val testDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testDispatcher)*/

        //database.clearAllTables()
        //database.IngredientDatabaseDao.clear()
        val db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            IngredientDatabase::class.java
        )
            .allowMainThreadQueries().build()
        ServiceLocator.setDatabase(db)

        repository = ((ApplicationProvider.getApplicationContext()) as App).taskRepository
        repository.saveNewIngredient(IngredientDataClass(1, "DescriptionM", 2, "url", "jpeg"))
        repository.saveNewIngredient(IngredientDataClass(2, "DescriptionQ", 3, "url", "png"))

        viewModel = IngredientViewModel(
            ApplicationProvider.getApplicationContext(),
            repository,
            nutritionApi
        )
        //mList = viewModel.mutableLiveDataList.value!!
    }

/*    @After
    fun close() = runBlocking {
        //database.IngredientDatabaseDao.clearIngredientEntity()
    }*/

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

        println("Current thread: ${Thread.currentThread().name}")

        //mainCoroutineRule.dispatcher.runBlockingTest {
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

       // val scenario = launchFragmentInContainer<IngredientListOverview>(Bundle(), R.style.AppTheme)
       // dataBindingIdlingResource.monitorFragment(scenario)

        onView(withId(R.id.searchIngredient)).perform(replaceText("Apple"), closeSoftKeyboard())
        onView(withId(R.id.shopping_cart)).check(matches(isDisplayed()))

        onView(withId(R.id.searchIngredientButton)).perform(click())
        onView(withId(R.id.progress_circular)).check(matches(isDisplayed()))

        delay(2000)

        onView(withId(R.id.recycler_view_network)).check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view_network)).perform(
            RecyclerViewActions.actionOnItemAtPosition<localIngredientAdapter.ViewHolder>
                (0, ViewActions.click())
        )

        onView(withId(R.id.addIngredientFAB)).perform(click())

        //val fragmentScenario = launchFragmentInContainer<IngredientListOverview>(Bundle(), R.style.AppTheme)
        //dataBindingIdlingResource.monitorFragment(fragmentScenario)
//        onView(withId(R.id.shopping_cart)).check(matches(not(isDisplayed())))
        //TODO: recycler_view_local not recognized despite being visible on screen
        //A: Needed to restart the activity
        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))

/* Tried:
 onView(withId(R.id.recycler_view_local)).inRoot(RootMatchers.withDecorView
            (`is`(getActivity(listActivityScenario)?.window?.decorView))).check(matches(isDisplayed()))

 onView(withId(R.id.recycler_view_local)).inRoot(RootMatchers.withDecorView(not(`is`(ContextUtils.getActivity
 (getApplicationContext())?.getWindow()?.getDecorView())))).check(matches(isDisplayed()))
 */

        onView(withId(R.id.recycler_view_local)).perform(
            RecyclerViewActions.actionOnItemAtPosition<localIngredientAdapter.ViewHolder>
                (0, ViewActions.click())
        )
        delay(2000)
        onView(withId(R.id.decreaseButton)).perform(click())

        onView(withId(R.id.increaseButton)).perform(click())
        onView(withText("1")).check(matches(isDisplayed()))
        onView(withId(R.id.addIngredientFAB)).perform(click())

        delay(2000)

        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))

        //checks item count of local reyclerView
        assertThat(returnItemCount(withId(R.id.recycler_view_local),3), `is`(true))

        //TODO: Why does "listOfSavedIngredients" have a null value?
        assertThat(viewModel.listOfSavedIngredients.value?.size, `is`(not(2)))

        onView(withId(R.id.search_recipe)).perform(click())

        onView(withText("Select at least one ingredient")).inRoot(
            RootMatchers.withDecorView(
                Matchers.not(
                    `is`(
                        getActivity(
                            listActivityScenario
                        )
                            ?.window?.decorView
                    )
                )
            )
        ).check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view_local)).perform(
            RecyclerViewActions.actionOnItemAtPosition<localIngredientAdapter.ViewHolder>
                (0, MyViewAction.clickChildViewWithId(R.id.checkbox))
        )

        onView(withId(R.id.recycler_view_local))
            .check(matches(withItemCount(3)))

        onView(withId(R.id.search_recipe)).perform(click())

        delay(3000)
        assertThat(mGetText(withId(R.id.add_ingredient_chip)), `is`(not(nullValue())))
        assertThat(mGetText(withId(R.id.add_ingredient_chip)), containsString("apple"))
        assertThat(checkForDuplicatesViewAction(withId(R.id.add_ingredient_chip), R.id.add_ingredient_chip), `is`(false))

        //onView(withId(R.id.ingredient_list)).check(matches(checkForDuplicates()))

        //assertThat(checkForDuplicates(), `is`(false))

        //Select at least one ingredient
        //onView(withId(R.id.test)).che.ck(matches(withText(not(containsString("NutritionApp")))))
        activityScenario.close()
        listActivityScenario.close()
    }

    //source: https://stackoverflow.com/questions/28476507/using-espresso-to-click-view-inside-recyclerview-item
    object MyViewAction {
        fun clickChildViewWithId(id: Int): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View>? {
                    return null
                }

                override fun getDescription(): String {
                    return "Click on a child view with specified id."
                }

                override fun perform(uiController: UiController?, view: View) {
                    val v = view.findViewById<View>(id)
                    v.performClick()
                }
            }
        }
    }

    //Issue: Returning nothing
    //try BoundedMatcher as opposed to ViewAction
    fun checkForDuplicates() : Matcher<View>{
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("Trying BoundedMatcher")
            }

            override fun matchesSafely(item: TextView?): Boolean {
                if (item != null)
                {
                val result: List<String>? = item.text?.toString()?.split("\n")?.map { it.trim() }
                println("list size:\n ${result?.size}")
                return result?.toSet()?.toList()?.size == result?.size
                    }
                else
                    return false
            }
        }
    }

    fun withItemCount(count: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("RecyclerView with item count: $count")
            }

            override fun matchesSafely(item: RecyclerView?): Boolean {
                return item?.adapter?.itemCount == count
            }
        }
    }

    fun checkForDuplicatesViewAction(matcher: Matcher<View>, id : Int): Boolean
    {
        var bool = false
        onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController?, view: View) {
                val mView = view.findViewById<TextView>(id)

                val result: List<String>? = mView.text?.toString()?.split("\n")?.map { it.trim() }
                println("list size:\n ${result?.size}")

                bool = result?.toSet()?.toList()?.size == result?.size
            }
        })
            return bool
    }

    //technically you can make BoundedMatcher return a Boolean as shown below
    fun mCheckForDuplicatesViewAction(matcher: Matcher<View>): Boolean
    {
        var bool = false
         (object : BoundedMatcher<View, EditText>(EditText::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("SomeText")
            }

            override fun matchesSafely(item: EditText?): Boolean {
                if (item != null)
                {
                    val result: List<String>? = item.text?.toString()?.split("\n")?.map { it.trim() }
                    println("list size:\n ${result?.size}")

                    bool = result?.toSet()?.toList()?.size == result?.size
                }
            return bool
            }

        })
        return bool
    }



//source: https://stackoverflow.com/questions/23381459/how-to-get-text-from-textview-using-espresso

    //creating a custom view Matcher using Espresso
    //note that ViewAction overrides three methods
    fun mGetText(matcher: Matcher<View?>?): String {
        val stringBuilder = StringBuilder()
        //espresso code
        onView(matcher).perform(object : ViewAction {

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController?, view: View) {
                val tv = view as TextView //Save, because of check in getConstraints()
                //can also get count of recyclerView
                //val size = (view as RecyclerView).adapter?.itemCount
                stringBuilder.append(tv.text.toString())
            }
        })
        return stringBuilder.toString()
    }

    fun returnItemCount(matcher: Matcher<View>, count : Int) : Boolean
    {
        var bool = false
        onView(matcher).perform(object : ViewAction {

              override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(RecyclerView::class.java)
              }

              override fun getDescription(): String {
                  return "getting item count from a RecyclerView"
              }

              override fun perform(uiController: UiController?, view: View?) {
                   bool = ((view as RecyclerView).adapter?.itemCount == count)
              }
        })
        return bool
    }


    var DEFAULT_WAIT_TIMEOUT = 10000L
    var DEFAULT_SLEEP_INTERVAL = 10000L
    fun waitUntilCondition(
        matcher: Matcher<View>,
        timeout: Long = DEFAULT_WAIT_TIMEOUT,
        condition: (View?) -> Boolean
    ) {

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

    // get activity context
    private fun getActivity(activityScenario: ActivityScenario<IngredientListActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    fun clickIn(x: Double, y: Double): ViewAction {
        return GeneralClickAction(
            Tap.LONG,
            CoordinatesProvider { view ->
                val screenPos = IntArray(2)
                view?.getLocationOnScreen(screenPos)

                val screenX = (screenPos[0] + x).toFloat()
                val screenY = (screenPos[1] + y).toFloat()

                floatArrayOf(screenX, screenY)
            },
            Press.PINPOINT,
            InputDevice.SOURCE_ANY,
            MotionEvent.BUTTON_PRIMARY
        )
    }
}