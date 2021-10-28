package com.example.nutritionapp

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.IngredientDatabase
import com.example.nutritionapp.database.IngredientRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        /**
         * use Koin Library as a service locator
         * Koin is a framework for dependency injection. First, we define some classes in the "module{}"
         * of Koin, such as:
        single { RemindersLocalRepository(get()) as ReminderDataSource }
        single { LocalDB.createRemindersDao(this@MyApp) }
        .
         * This lets Koin choose from these defined classes when we fill the parameter of say "RemindersListViewModel" or
         * "SaveReminderViewModel" when we call "get()" inside of them. "get()" therefore is like an open ended sentence which
         * is filled with the defined classes in the Koin module.
         * Recall that dependency injection allows us greater flexibility in passing altering classes:
         * https://www.youtube.com/watch?v=eH9UrAwKEcE&ab_channel=TheObenCode
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            single {
                IngredientViewModel(
                    get(),get() as IngredientDataSourceInterface
                )
            }
            single { IngredientRepository(get()) as IngredientDataSourceInterface }
            single { IngredientDatabase.getInstance(this@App) }            //Declare singleton definitions to be later injected using by inject()
            //"single" is in contrast to "factory", which creates a new instance every time

/*            single {
                //This view model is declared singleton to be used across multiple fragments
                //alternatively:
                SaveReminderViewModel(
                    get(),get() as ReminderDataSource
                )
                //  viewModel { SaveReminderViewModel(get(), get() as ReminderDataSource) }
            }*/

            //single{ ApplicationProvider.getApplicationContext()}
          /*  single {ServiceLocator.provideTasksRepository(applicationContext)}
            //single { RemindersLocalRepository(get()) as ReminderDataSource } //replaced by ServiceLocator
            single { LocalDB.createRemindersDao(this@MyApp) }*/
        }

        startKoin {
            androidContext(this@App)
            modules(listOf(myModule))
        }

    }
}