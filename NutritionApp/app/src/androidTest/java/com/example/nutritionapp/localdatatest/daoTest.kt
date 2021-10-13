package com.example.nutritionapp.localdatatest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.endtoendtest.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class daoTest {

    private lateinit var database: IngredientDatabase

    //ensure LiveData is handled synchronously
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get: Rule
    val mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        //source for setTransactionExecutor: https://medium.com/@eyalg/testing-androidx-room-kotlin-coroutines-2d1faa3e674f
        //if we don't specify the default dispatchers, a different default dispatcher will be provided, which will create a different scope
        //that is beyond the ones created in the DAO functions
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            IngredientDatabase::class.java
        ).setTransactionExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .setQueryExecutor(mainCoroutineRule.dispatcher.asExecutor())
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries().build()
        database.clearAllTables()
    }

    @After
    fun close() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun testDao() = mainCoroutineRule.runBlockingTest {
        //given an ingredient item
        val ingredientItem = IngredientDataClassDTO(5,"name",1,"url","JPEG")

        //when user saves an item
        database.IngredientDatabaseDao.saveIngredient(ingredientItem)

        //then the item can be retrieved
        database.IngredientDatabaseDao.deleteIngredientById(ingredientItem.id)
        val returnedItem = database.IngredientDatabaseDao.getIngredientByIdTest(ingredientItem.id)
        val allItem = database.IngredientDatabaseDao.getAllIngredients()

        //TODO: returnedItem is null
        assertThat(returnedItem, `is`(ingredientItem) )

        println("name: ${returnedItem?.name}")

        assertThat(database, notNullValue())
        assertThat(allItem, notNullValue())
        assertThat(returnedItem, notNullValue())
    }
}