package com.example.nutritionapp.database

import androidx.room.*
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.menu.GeofenceReferenceData
import com.example.nutritionapp.recipeofday.RecipeOfDay

@Dao
interface IngredientDao {

/*    @Insert
    suspend fun insert(ingredient: IngredientDataClassDTO)*/

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param ingredient new value to write
     */
    @Update
    suspend fun update(ingredient: IngredientDataClassDTO)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    //Note: When returning a LiveData from a DAO function, you do not need to call suspend on said function as suspend and LiveData
    //are incompatible (source:https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth)


    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM Ingredient_Entity")
    suspend fun clearIngredientEntity()

    @Query("DELETE FROM RecipeEntity")
    suspend fun clearRecipeEntity()
    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    //Note: If your DAO function returns a live data then you cannot modify the DAO function with a suspend and as
    //such you cannot expect the DAO function to run synchronously in testing by default.
    // See: https://stackoverflow.com/questions/44270688/unit-testing-room-and-livedata for solution ; this is why
    //we used the getOrAwait function in Udacity003 Project
    @Query("SELECT * FROM Ingredient_Entity ORDER BY id DESC")
    suspend fun getAllIngredients(): List<IngredientDataClassDTO>?

    /**
     * Selects and returns the latest ingredient.
     */
    @Query("SELECT * FROM Ingredient_Entity ORDER BY id DESC LIMIT 1")
     suspend fun getLastIngredient(): IngredientDataClassDTO?

    /**
     * Selects and returns the ingredient with given nightId.
     */
    @Query("SELECT * from Ingredient_Entity WHERE id = :key")
    suspend fun getIngredientById(key: Int): IngredientDataClassDTO?

/*    @Query("SELECT * from Ingredient_Entity WHERE id = :key")
     fun getIngredientByIdTest(key: Int): LiveData<IngredientDataClassDTO?>*/

    //delete ingredient
    @Query("DELETE from Ingredient_Entity WHERE id = :key")
     fun deleteIngredientById(key: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveIngredient(ingredient: IngredientDataClassDTO)





    @Query("SELECT * from RecipeEntity WHERE id = :key")
    suspend fun getNotificationRecipeById(key: String): RecipeNotificationClassDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNotificationRecipe(recipeDTO: RecipeNotificationClassDTO)

    @Query("SELECT * FROM RecipeEntity")
    suspend fun getAllRecipeNotification() : List<RecipeNotificationClassDTO>?

    @Query("DELETE FROM RecipeEntity WHERE id = :key")
    suspend fun deleteRecipeNotificationById(key : String)

    @Query("SELECT * FROM RecipeOfDay")
    suspend fun getRecipeOfDay() : RecipeOfDay

    @Insert
    suspend fun saveRecipeOfDay(rod : RecipeOfDay)

    @Insert
    suspend fun saveGeofenceReferenceData(data : GeofenceReferenceData)

    @Query("SELECT * FROM GeofenceReferenceData WHERE id = :key")
    suspend fun returnGeofenceReferenceData(key : String) : GeofenceReferenceData
}


