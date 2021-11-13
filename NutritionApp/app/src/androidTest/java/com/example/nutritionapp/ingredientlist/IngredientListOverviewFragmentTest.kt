package com.example.nutritionapp.ingredientlist

import ServiceLocator
import android.os.Bundle
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nutritionapp.App
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.util.Result
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.verify


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class IngredientListOverviewFragmentTest {

    private lateinit var repository: IngredientDataSourceInterface

@get: Rule
val instantTaskExecutorRule = InstantTaskExecutorRule()

    //we want to isolate this fragment for testing so we create a fake repository to minimize dependencies

    @Before
    fun initializeRepository()
    {
        //TODO: How can I substitute a fakeRepository for the fragment if I am using sharedViewModel?

        /*fakeRepository = fakeIngredientRepository()
        ServiceLocator.ingredientRepository = fakeRepository*/

        //Intents.init()
        ServiceLocator.resetRepository()

        repository = ((ApplicationProvider.getApplicationContext()) as App).taskRepository

        runBlocking {
            repository.saveNewIngredient(IngredientDataClass(1, "DescriptionM", 2, "url", "jpeg"))
            repository.saveNewIngredient(IngredientDataClass(2, "DescriptionQ", 3, "url", "png"))
        }
    }

    @After
    fun cleanUp() = runBlockingTest{
    ServiceLocator.resetRepository()
    }


    @Test
    fun ingredientListFragment_DisplayAndNavigation_Test(): Unit = runBlocking{
        //given - IngredientListOverview fragment and an ingredientItem
        val scenario = launchFragmentInContainer<IngredientListOverview>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        val ingredientItem = IngredientDataClass(1, "name", 1, "url", "Jpeg")

        repository.saveNewIngredient(ingredientItem)

        val result = repository.getIngredients() as Result.Success

        onView(withId(R.id.shopping_cart)).check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))

        onView(withId(R.id.searchIngredientButton)).perform(click())

        onView(withText("Enter ingredient to search")).inRoot(
            withDecorView(
                not(`is`(getActivity(ApplicationProvider.getApplicationContext())?.getWindow()?.getDecorView())))).check(matches(isDisplayed()))

        delay(3000)

        onView(withId(R.id.search_recipe)).perform(click())

        //source: https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso
        onView(withText("Select at least one ingredient")).inRoot(
            withDecorView(
                not(`is`(getActivity(ApplicationProvider.getApplicationContext())?.getWindow()?.getDecorView())))).check(matches(isDisplayed()))


        onView(withId(R.id.recycler_view_local)).check(matches(hasDescendant(withText("DescriptionQ"))))

        //Q: not sure why receiving PerformException

       /* onView(withId(R.id.recycler_view_local)).perform(RecyclerViewActions.actionOnItem<localIngredientAdapter.ViewHolder>(
            (hasDescendant(withText("DescriptionQ"))), ViewActions.click()))

                   onView(withId(R.id.recycler_view_local)).perform(actionOnHolderItem(customViewMatcher("DescriptionQ"), click()))

*/


//previous answer
  /*      onView(withId(R.id.recycler_view_local))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText("DescriptionQ")), click()
                )
            )*/

        //source: https://stackoverflow.com/questions/29556883/how-to-click-on-action-bar-items-when-testing-with-android-espresso
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)

        Thread.sleep(3000)

        onView(
            withText(R.string.google_maps)
        ).check(matches(isDisplayed()))

        onView(
            withText(R.string.about)
        ).check(matches(isDisplayed()))

        //suggested by mentor:
        onView(withId(R.id.recycler_view_local)).perform(
            RecyclerViewActions.actionOnItemAtPosition<localIngredientAdapter.ViewHolder>
                (0, ViewActions.click())
        )

        //onView(withId(R.id.recycler_view_local)).perform(RecyclerViewActions.scrollTo<localIngredientAdapter.ViewHolder> (hasDescendant (withText ("DescriptionQ"))))

        verify(navController).navigate(IngredientListOverviewDirections.actionIngredientListOverviewToIngredientDetail())


    }

    //source: https://sisik.eu/blog/android/tests/recyclerview-custom-matcher
    fun customViewMatcher(packageName: String): Matcher<RecyclerView.ViewHolder> {
        return object : BoundedMatcher<RecyclerView.ViewHolder, localIngredientAdapter.ViewHolder>(
            localIngredientAdapter.ViewHolder::class.java
        ){
            override fun describeTo(description: Description?) {
                description?.appendText("with item package name " + packageName)
            }

            override fun matchesSafely(item: localIngredientAdapter.ViewHolder?): Boolean {
                return item?.binding?.ingredientName!!.equals(packageName)
            }
        }
    }


    fun hasItem(matcher: Matcher<View?>): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item: ")
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val adapter = view.adapter
                for (position in 0 until adapter!!.itemCount) {
                    val type = adapter.getItemViewType(position)
                    val holder = adapter.createViewHolder(view, type)
                    adapter.onBindViewHolder(holder, position)
                    if (matcher.matches(holder.itemView)) {
                        return true
                    }
                }
                return false
            }
        }
    }
    fun withItemCount(mString: String): Matcher<RecyclerView.ViewHolder> {
        return object : BoundedMatcher<RecyclerView.ViewHolder, localIngredientAdapter.ViewHolder>
            (localIngredientAdapter.ViewHolder::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("RecyclerView")
            }

            override fun matchesSafely(item: localIngredientAdapter.ViewHolder?): Boolean {
                if (item != null) {
                    return item.binding.ingredientName.equals(mString)
                }
                return false
            }
        }
    }


}


