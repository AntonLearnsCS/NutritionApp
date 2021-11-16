package com.example.nutritionapp.localdatatest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.database.IngredientRepository
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.util.succeeded
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class repositoryTest {
private lateinit var database : IngredientDatabase
private lateinit var repository : IngredientRepository
@get:Rule
val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            IngredientDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = IngredientRepository(database.IngredientDatabaseDao)

    }
    @After
    fun cleanUp() {
        database.close()
    }

    // runBlocking used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204

    @Test
    fun repository_saveIngredient_returnResult() = runBlocking{
        //given - saved ingredient
        val someIngredient = IngredientDataClass(1,"name",1,"url","Jpeg")
        repository.saveNewIngredient(someIngredient)

        // when - retrieve ingredient
        val resultSuccess = repository.getIngredient(someIngredient.id)
        assertThat(resultSuccess.succeeded,`is`(true))
        resultSuccess as Result.Success

        val resultError = repository.getIngredient(888)
        assertThat(resultError.succeeded,`is`(false))

        assertThat(resultSuccess.data.name, `is`(someIngredient.name))
        Assert.assertThat(resultSuccess.data.imageType, `is`("Jpeg"))
        Assert.assertThat(resultSuccess.data.image, `is`("url"))
    }

    @Test
    fun repository_GetIngredients_ReturnError() = runBlocking{
        //given - an empty local repository
        repository.deleteAllIngredients()

        //when - user retrieves a data item that does not exists
        val resultIngredients = repository.getIngredients()
        //then - an error is returned
        MatcherAssert.assertThat(
            resultIngredients,
            `is`(Result.Error
                ("No ingredients found"))
        )
    }
}