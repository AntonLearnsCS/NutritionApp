package com.example.nutritionapp.endtoendtest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4

import androidx.test.filters.LargeTest
import com.example.nutritionapp.R
import com.example.nutritionapp.authentication.AuthenticationActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class endtoendtest {
@get:Rule
val instanceTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun endToEndTest(): Unit = runBlocking {
        //activityScenario is part of AndroidX testing
        val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).perform(click())
        delay(3000)
        onView(withId(R.id.recycler_view_local)).check(matches(isDisplayed()))
    }
}