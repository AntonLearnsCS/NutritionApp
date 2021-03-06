import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.database.IngredientRepository
import com.example.nutritionapp.database.MIGRATION_10_11
import com.example.nutritionapp.network.mNutritionApi
import kotlinx.coroutines.runBlocking
val MIGRATION_9_10: Migration = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo
        database.execSQL("CREATE TABLE IF NOT EXISTS `RecipeOfDay` (`id` INTEGER NOT NULL, `image` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }
}
val MIGRATION_11_12: Migration = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo
        database.execSQL("ALTER TABLE `RecipeEntity` ADD COLUMN `image` TEXT")
    }
}
//We use a ServiceLocator in order to return an instance of a repository and other dependencies
object ServiceLocator {

    private val lock = Any()
    private var database: IngredientDatabase? = null
    //volatile - meaning that writes to this field are immediately made visible to other threads
    // since multiple threads can request a repository. This is accomplished by: Volatile fields provide memory
    // visibility and guarantee that the value that is being read, comes from the main memory and not the cpu-cache
    @Volatile
    var ingredientRepository: IngredientDataSourceInterface? = null
        @VisibleForTesting set
    //"@VisibleForTesting set" allows you to set the value for this variable, so we can set the
    //repository to a fake repository

    fun provideTasksRepository(context: Context): IngredientDataSourceInterface {
        //recall that synchronized means that only one thread can call this function at a time like a room with a key with only
        //person allowed in at one time. Here, "this", which is the ServiceLocator object acts as the key
        synchronized(this) {
            return ingredientRepository ?: createTaskLocalDataSource(context)
        }
    }

    object mNutritionObject : mNutritionApi()

    fun returnNutritionApi() : mNutritionApi
    {
        synchronized(this)
        {
        return mNutritionObject
        }
    }


    private fun createTaskLocalDataSource(context: Context): IngredientDataSourceInterface {
        val database = database ?: createDataBase(context)
        return IngredientRepository(database.IngredientDatabaseDao)
    }

    private fun createDataBase(context: Context): IngredientDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            IngredientDatabase::class.java, "Ingredient.db"
        ).fallbackToDestructiveMigration()
            .build()

        database = result
        return result
    }

    @VisibleForTesting
    fun setDatabase( db : IngredientDatabase)
    {
        database = db
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                ingredientRepository?.deleteAllIngredients()
                ingredientRepository?.clearNotificationRecipe()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            ingredientRepository = null
        }
    }
}
