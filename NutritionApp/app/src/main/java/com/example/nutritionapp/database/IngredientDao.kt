package com.example.nutritionapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nutritionapp.database.dto.IngredientDataClassDTO

@Dao
interface IngredientDao {

    @Insert
    suspend fun insert(ingredient: IngredientDataClass)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param ingredient new value to write
     */
    @Update
    suspend fun update(ingredient: IngredientDataClass)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from ingredient_entity WHERE id = :key")
    suspend fun get(key: Long): LiveData<IngredientDataClassDTO?>

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM ingredient_entity")
    suspend fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM Ingredient_Entity ORDER BY id DESC")
    fun getAllIngredients(): LiveData<List<IngredientDataClassDTO>>

    /**
     * Selects and returns the latest ingredient.
     */
    @Query("SELECT * FROM Ingredient_Entity ORDER BY id DESC LIMIT 1")
    suspend fun getLastIngredient(): LiveData<IngredientDataClassDTO?>

    /**
     * Selects and returns the ingredient with given nightId.
     */
    @Query("SELECT * from Ingredient_Entity WHERE id = :key")
    fun getIngredientById(key: String): LiveData<IngredientDataClassDTO>

    //delete ingredient
    @Query("DELETE from Ingredient_Entity WHERE id = :key")
    fun deleteIngredientById(key: String): LiveData<IngredientDataClassDTO>
}