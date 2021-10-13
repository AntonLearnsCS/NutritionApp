package com.example.nutritionapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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
    @Query("SELECT * from Ingredient_Entity WHERE id = :key")
     fun get(key: Double): LiveData<IngredientDataClassDTO?>

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM Ingredient_Entity")
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
     fun getLastIngredient(): LiveData<IngredientDataClassDTO?>

    /**
     * Selects and returns the ingredient with given nightId.
     */
    @Query("SELECT * from Ingredient_Entity WHERE id = :key")
    fun getIngredientById(key: Int): LiveData<IngredientDataClassDTO>

    //delete ingredient
    @Query("DELETE from Ingredient_Entity WHERE id = :key")
    fun deleteIngredientById(key: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveIngredient(ingredient: IngredientDataClassDTO)
}