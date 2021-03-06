package com.example.nutritionapp.database

/*
import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

/**
 * The Room Database that contains the reminders table.
 */
@Database(entities = [ReminderDTO::class], version = 1, exportSchema = false)
abstract class RemindersDatabase : RoomDatabase() {
    abstract fun reminderDao(): RemindersDao
}
 */
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDTO
import com.example.nutritionapp.menu.GeofenceReferenceData
import com.example.nutritionapp.recipe.RecipeIngredientResultDTO
import com.example.nutritionapp.recipe.RecipeIngredientResultNetwork
import com.example.nutritionapp.recipeofday.RecipeOfDay
val MIGRATION_9_10: Migration = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo
        database.execSQL("CREATE TABLE IF NOT EXISTS `RecipeOfDay` (`id` INTEGER NOT NULL, `image` TEXT NOT NULL, PRIMARY KEY(`id`))")//"CREATE TABLE IF NOT EXISTS `RecipeOfDay` (`id` INTEGER NOT NULL, `image` TEXT NOT NULL)")
    }
}
val MIGRATION_10_11: Migration = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo
        database.execSQL("CREATE TABLE IF NOT EXISTS `GeofenceReferenceData` (`id` TEXT NOT NULL, `recipeName` TEXT NOT NULL, `missingIngredients` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }
}
val MIGRATION_11_12: Migration = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo
        database.execSQL("ALTER TABLE `RecipeEntity` ADD COLUMN `image` TEXT")
    }
}
@Database(entities = [IngredientDataClassDTO::class, RecipeNotificationClassDTO::class, RecipeOfDay::class, GeofenceReferenceData::class, RecipeIngredientResultDTO::class], version = 2, exportSchema = false)
abstract class IngredientDatabase : RoomDatabase() {

    /**
     * Connects the database to the DAO.
     */
    abstract val IngredientDatabaseDao: IngredientDao
    /**
     * Define a companion object, this allows us to add functions on the IngredientDatabase class.
     *
     * For example, clients can call `IngredientDatabase.getInstance(context)` to instantiate
     * a new IngredientDatabase.
     */
    companion object {
        /**
         * INSTANCE will keep a reference to any database returned via getInstance.
         *
         * This will help us avoid repeatedly initializing the database, which is expensive.
         *
         *  The value of a volatile variable will never be cached, and all writes and
         *  reads will be done to and from the main memory. It means that changes made by one
         *  thread to shared data are visible to other threads.
         */
        @Volatile
        private var INSTANCE: IngredientDatabase? = null

        /**
         * Helper function to get the database.
         *
         * If a database has already been retrieved, the previous database will be returned.
         * Otherwise, create a new database.
         *
         * This function is threadsafe, and callers should cache the result for multiple database
         * calls to avoid overhead.
         *
         * This is an example of a simple Singleton pattern that takes another Singleton as an
         * argument in Kotlin.
         *
         * To learn more about Singleton read the wikipedia article:
         * https://en.wikipedia.org/wiki/Singleton_pattern
         *
         * @param context The application context Singleton, used to get access to the filesystem.
         */
        fun getInstance(context: Context): IngredientDatabase {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {
                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // Smart cast is only available to local variables.
                var instance = INSTANCE
                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        IngredientDatabase::class.java,
                        "saved_ingredient_database"
                    )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this lesson. You can learn more about
                        // migration with Room in this blog post:
                        // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                        .fallbackToDestructiveMigration()
                        .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }
                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}