package com.example.nutritionapp.database

import android.content.Context
import androidx.room.Room

object LocalDatabase {
    /**
     * Singleton class that is used to create a reminder db
     */
        /**
         * static method that creates a reminder class and returns the DAO of the reminder
         */
        fun createIngredientDao(context: Context): IngredientDao {
            return Room.databaseBuilder(
                context.applicationContext,
                IngredientDatabase::class.java, "locationReminders.db"
            ).build().IngredientDatabaseDao

            //we call .reminderDao() so that we don't have to call "abstract fun reminderDao(): RemindersDao" whenever we
            //want to access DAO methods when referencing a database instance
    }
}