package com.example.nutritionapp.ingredientlist

import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
//import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.nutritionapp.database.IngredientDataClass
import kotlinx.coroutines.launch
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.databinding.IngredientListRecyclerviewBinding
import com.example.nutritionapp.network.*
import com.example.nutritionapp.recipe.PostRequestResultWrapper
import com.example.nutritionapp.recipe.RecipeIngredientResult
import com.example.nutritionapp.recipe.RecipeIngredientResultWrapper
import com.example.nutritionapp.util.wrapEspressoIdlingResource

/*
To get context inside a ViewModel we can either extend AndroidViewModel. Do not use ApplicationProvider in production code, only in tests
https://stackoverflow.com/questions/51451819/how-to-get-context-in-android-mvvm-viewmodel
 */
class IngredientViewModel (val app: Application, val ingredientRepository : IngredientDataSourceInterface)
    : AndroidViewModel(app), Observable {

    val listOfRecipes = mutableListOf<RecipeIngredientResult>()
    val listOfRecipesLiveData = MutableLiveData<List<RecipeIngredientResult>>(listOfRecipes)

    //val selectedRecipe = MutableLiveData<RecipeIngredientResult>()

    var navigatorFlag = MutableLiveData<Boolean>(false)
//    val binding = IngredientListRecyclerviewBinding.inflate(LayoutInflater.from(ApplicationProvider.getApplicationContext()))
    var listOfNetworkRequestedIngredients : List<IngredientDataClass>? = null //getIngredientListByNetwork()

    var displayListInXml = mutableListOf<IngredientDataClass>()//<IngredientDataClass>()// = null
    //Note: Don't set the MutableLiveData to null, b/c technically it is not initialized so any assignment will not change the null value
    //and variable observing this MutableLiveData will return null
    var mutableLiveDataList : MutableLiveData<MutableList<IngredientDataClass>> = MutableLiveData()
    var listOfSavedIngredients : MutableLiveData<List<IngredientDataClass>> = MutableLiveData()//null//getLocalIngredientList()

    val testDTO = IngredientDataClass(4,"nameTest",2,"url","jpeg")

    //two-way binding
    //no need to add "?query=" since the getIngredients() of the IngredientsApiInterface will do that
    var searchItem = MutableLiveData<String>("Apple")
        set(value) {
            Log.i("test","search Item assigned")
            field = value
        }


    val selectedIngredient = MutableLiveData<IngredientDataClass>()
    val foodInText = mutableListOf<String>()
    val listOfIngredientsString = MutableLiveData<String>("Apple,flour,sugar")

        //input is list of names i.e {"Snapple Apple flavored drink 4oz","Mott's Apple pudding 3oz"}
     fun detectFoodInText(listName : List<String>) {
          viewModelScope.launch {
            try {
                for (i in listName)
                {
                    val listOfIngredients : PostRequestResultWrapper = NutritionAPI.nutritionServicePost.detectFoodInText(i)
                    for (g in listOfIngredients.annotations)
                    {
                        foodInText.add(g.annotation)
                    }
                }
            }
            catch (e: java.lang.Exception) {
                Log.i("Exception", "$e")
            }
              if (!foodInText.isEmpty())
                  listOfIngredientsString.value = foodInText.joinToString(separator = ",")
            }
     }
    fun findRecipeByIngredients()
    {
        viewModelScope.launch {
            try {
                val resultWrapper : RecipeIngredientResultWrapper = NutritionAPI.nutritionServiceGetRecipeIngredients.findByIngredients(listOfIngredientsString.value!!)
                for (i in resultWrapper.mList)
                {
                    listOfRecipes.add(i)
                }
                listOfRecipesLiveData.value = listOfRecipes
            }
            catch (e : java.lang.Exception)
            {
                Log.i("Exception","$e")
            }
        }
    }

    fun loadIngredientListByNetwork()
    {
        wrapEspressoIdlingResource {
            viewModelScope.launch {

                if (searchItem.value != null) {
                    try {
                        val result :wrapperIngredientListNetworkDataClass =
                            NutritionAPI.nutritionService.getIngredients(searchItem.value!!)
                        Log.i("test","search item: ${searchItem.value}")
                        println("Total products: ${result.totalProducts}")

                        listOfNetworkRequestedIngredients = result.products.map {
                            IngredientDataClass(
                                name = it.name, quantity = 1, id = it.id,
                                imageUrl = it.imageUrl, imageType = it.imageType
                            )
                        }
                        //assignment not working
                        displayListInXml.addAll(listOfNetworkRequestedIngredients!!)
                        println("Number of items displayListInXml: ${displayListInXml.size}")

                        mutableLiveDataList?.value = displayListInXml



                        println("Number of items: ${mutableLiveDataList?.value!!.size}")

                        for (i in listOfNetworkRequestedIngredients!!)
                        {
                            println("Ingredient name: ${i.name}")
                        }

                        //Note
                        Toast.makeText(
                            app,
                            "networkRequestSuccess",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    catch (e: Exception) {
                        println("Error: ${e.message}")
                    }

                } else {
                    Toast.makeText(
                        app,
                        "Missing search parameter",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    fun saveIngredientItem()
    {
        if(selectedIngredient.value != null && selectedIngredient.value?.quantity!! > 0) {
            wrapEspressoIdlingResource {
                viewModelScope.launch {
                    selectedIngredient.value?.let { ingredientRepository.saveNewIngredient(it) }
                }
            }
        }
        else
            Toast.makeText(app,"Invalid quantity",Toast.LENGTH_SHORT).show()
    }

    fun getLocalIngredientList()
    { //need DAO and repository
        wrapEspressoIdlingResource {
        viewModelScope.launch {

            val ingredientResult : Result<List<IngredientDataClassDTO>> = ingredientRepository.getIngredients()
             when(ingredientResult)
             {
                 is Result.Success<*> ->
                 {
                     val dataList = ArrayList<IngredientDataClass>()
                     dataList.addAll((ingredientResult.data as List<IngredientDataClassDTO>).map { result ->
                         //map the reminder data from the DB to the be ready to be displayed on the UI
                         IngredientDataClass(
                             id = result.id,
                             name = result.name,
                             quantity = result.quantity,
                             imageUrl = result.image,
                             imageType = result.imageType
                         )
                     })
                     listOfSavedIngredients?.value = dataList
                 }
                is Result.Error ->
                {
                    Log.i("test","empty repository")
//                Toast.makeText(ApplicationProvider.getApplicationContext(),"${ingredientResult.message}",Toast.LENGTH_SHORT).show()
                }
             }
        }
        }
    }
    private val _navigateToDetail = MutableLiveData<IngredientDataClass>()
    val navigateToDetail : LiveData<IngredientDataClass>
    get() = _navigateToDetail

    fun setNavigateToDetail(ingredientItem : IngredientDataClass)
    {
        _navigateToDetail.value = ingredientItem
    }
    fun setNavigateToDetailNull()
    {
        _navigateToDetail.value = null
    }


    private val _navigateToRecipe = MutableLiveData<RecipeIngredientResult>()
    val navigateToRecipe : LiveData<RecipeIngredientResult>
        get() = _navigateToRecipe
    fun setNavigateToRecipe(recipe : RecipeIngredientResult)
    {
        _navigateToRecipe.value = recipe
    }
    fun setNavigateToRecipeNull()
    {
        _navigateToRecipe.value = null
    }

    @Override
    override fun onCleared() {
        super.onCleared()
    }


    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}