package com.example.nutritionapp.ingredientlist

import android.os.Bundle
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.nutritionapp.App
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.IngredientRepository
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.localdatatest.fakeIngredientRepository
import com.example.nutritionapp.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.hasItems
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

        val ingredientItem = IngredientDataClass(1,"name",1,"url","Jpeg")

        repository.saveNewIngredient(ingredientItem)

        val result = repository.getIngredients() as Result.Success

        onView(withId(R.id.shopping_cart)).check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))

        delay(2000)
        //onView(withId(R.id.recycler_view_local)).check(matches((hasItem(hasDescendant(withText("TitleZ"))))))

        onView(withId(R.id.recycler_view_local)).check(matches(hasDescendant(withText("DescriptionQ"))))

        onView(withId(R.id.recycler_view_local)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>
            (
                ViewMatchers.hasDescendant(ViewMatchers.withText("DescriptionM")),
                ViewActions.click()
            ))
        //verify(navController).navigate(IngredientListOverviewDirections.actionIngredientListOverviewToIngredientDetail())

        delay(2000)

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
}
