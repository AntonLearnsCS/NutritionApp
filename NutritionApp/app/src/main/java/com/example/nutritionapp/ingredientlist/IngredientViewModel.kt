package com.example.nutritionapp.ingredientlist

//import androidx.test.core.app.ApplicationProvider

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.maps.RecipeNotificationClassDomain
import com.example.nutritionapp.network.*
import com.example.nutritionapp.recipe.*
import com.example.nutritionapp.recipe.intolerancespinnerclasses.IntoleraceDataType
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.net.URLEncoder
import kotlinx.coroutines.flow.*
import timber.log.Timber

/*
To get context inside a ViewModel we can either extend AndroidViewModel. Do not use ApplicationProvider in production code, only in tests
https://stackoverflow.com/questions/51451819/how-to-get-context-in-android-mvvm-viewmodel
 */

val selectedProductName = MutableLiveData<String>("Apple,flour,sugar")

class IngredientViewModel(
    val app: Application,
    val ingredientRepository: IngredientDataSourceInterface, val nutritionApi: mNutritionApi
) : AndroidViewModel(app) {

//--------------------------------------------------------IngredientListOverview Fragment-----------------------------------------------
    //Note: Don't set the MutableLiveData to null, b/c technically it is not initialized so any assignment will not change the null value
    //and variable observing this MutableLiveData will return null

    //flag for shopping cart image
    private val _shoppingCartVisibilityFlag = MutableLiveData(true)
    val shoppingCartVisibilityFlag: LiveData<Boolean>
        get() = _shoppingCartVisibilityFlag

    //flag for navigating once coroutine involving search recipe button in ingredientListFragment is finished
    private val _navigatorFlag = MutableLiveData<Boolean>(false)
    val navigatorFlag: LiveData<Boolean>
        get() = _navigatorFlag

    fun setNavigatorFlag(boolean: Boolean) {
        _navigatorFlag.value = boolean
    }

    private val _viewVisibilityFlag = MutableStateFlow<Boolean>(false)
    val viewVisibilityFlag: StateFlow<Boolean>
        get() = _viewVisibilityFlag


    private val _mutableLiveDataList: MutableLiveData<List<IngredientDataClass>> = MutableLiveData()
    val mutableLiveDataList: MutableLiveData<List<IngredientDataClass>>
        get() = _mutableLiveDataList

    private val _networkResultStateFlow = MutableStateFlow<List<IngredientDataClass>?>(null)
    val networkResultStateFlow : StateFlow<List<IngredientDataClass>?>
    get() = _networkResultStateFlow

    private val _listOfSavedIngredients = MutableStateFlow<List<IngredientDataClass>?>(null)
    val listOfSavedIngredients: StateFlow<List<IngredientDataClass>?>
    get() = _listOfSavedIngredients

    //two-way binding
    //no need to add "?query=" since the getIngredients() of the IngredientsApiInterface will do that
    var searchItem = MutableLiveData<String>()

    val foodInText = mutableListOf<String>()

    private val _navigateToDetail = MutableLiveData<IngredientDataClass>()
    val navigateToDetail: LiveData<IngredientDataClass>
        get() = _navigateToDetail

    fun setNavigateToDetail(ingredientItem: IngredientDataClass) {
        selectedIngredient.value = ingredientItem
        _quantityCounter.value = ingredientItem.quantity
        _navigateToDetail.value = ingredientItem
    }

    fun setNavigateToDetailNull() {
        _navigateToDetail.value = null
    }

    //--------------------------------------------------------mapGroceryReminder--------------------------------------------------------

    private val _latLng = MutableLiveData<LatLng>()
    val latLng: LiveData<LatLng>
        get() = _latLng

    fun setLatLng(latLng: LatLng) {
        _latLng.value = latLng
    }

    private val _saveRecipeNotificationFlag = MutableLiveData(false)


    //--------------------------------------------------------ingredientDetail Fragment ---------------------------------------------------
    val selectedIngredient = MutableLiveData<IngredientDataClass>()

    private val _quantityCounter = MutableLiveData<Int>(1)
    val quantityCounter: LiveData<Int>
        get() = _quantityCounter

    fun increaseQuantityCounter() {
        val temp = _quantityCounter.value?.plus(1)
        _quantityCounter.value = temp
        selectedIngredient.value?.quantity = temp!!
    }

    fun decreaseQuantityCounter() {
        if (_quantityCounter.value!! > 0) {
            //note - we need to reassign the mutableLiveData to a new variable for observers to observe a change in value
            //you can't just update the existing value inside the mutableLiveData
            val temp = _quantityCounter.value!!.minus(1)
            _quantityCounter.value = temp
            selectedIngredient.value?.quantity = temp
        }
    }

    //--------------------------------------------------------recipeDetail Fragment ---------------------------------------------------
    private val _missingIngredients = MutableLiveData<List<String>>()
    val missingIngredients: LiveData<List<String>>
        get() = _missingIngredients

    private val _listOfStepsLiveData = MutableLiveData<List<String>>()
    val listOfStepsLiveData: LiveData<List<String>>
        get() = _listOfStepsLiveData

    fun setMissingIngredientsNull() {
        _missingIngredients.value = null
    }

    //--------------------------------------------------------SearchRecipe Fragment--------------------------------------------------------
    //Q: Why does "val listOfRecipesLiveData = MutableLiveData<List<RecipeIngredientResult>>(listOfRecipes)" result in RecyclerView
    //being empty?
    //A: "val listOfRecipesLiveData : MutableLiveData<List<RecipeIngredientResult>>? = null" is explicitly setting the list<RecipeIngredientResult> value
    //to null so that even if we add new values to the list<RecipeIngredientResult> the observers are not notified since MutableLiveData is still observing
    //the same list. As such, you need to set a new list instead of updating the existing list since the list reference does not technically change when you
    //add new values to the list so the observers are not notified. (?) "MutableLiveData<List<RecipeIngredientResult>>()" is correct because initially there
    //is not value explicitly assigned to list<RecipeIngredientResult> so the first time a list is assigned the observers are signaled.
    //A work around is offered at source: https://stackoverflow.com/questions/61834480/livedata-is-not-triggered-when-list-item-is-getting-updated


    val _listOfRecipesLiveData = MutableLiveData<List<RecipeIngredientResult>>()
    val listOfRecipesLiveData: LiveData<List<RecipeIngredientResult>>
        get() = _listOfRecipesLiveData

    val searchRecipeEditTextFlag = MutableLiveData(false)

    fun setSearchRecipeEditTextClear() {
        foodInText.clear()

    }

    //Two-way data binding
    val ingredientToBeAddedAsChip = MutableLiveData<String>()

    private var _navigateToRecipeFlag = MutableLiveData(false)
    val navigateToRecipeFlag: LiveData<Boolean>
        get() = _navigateToRecipeFlag

    fun setNavigateToRecipeFlag(boolean: Boolean) {
        _navigateToRecipeFlag.value = boolean
    }

    private val _navigateToRecipe = MutableLiveData<RecipeIngredientResult>()
    val navigateToRecipe: LiveData<RecipeIngredientResult>
        get() = _navigateToRecipe

    fun setNavigateToRecipe(recipe: RecipeIngredientResult) {
        _navigateToRecipe.value = recipe
    }

    fun setNavigateToRecipeNull() {
        _navigateToRecipe.value = null
    }



    val arrayOfRecipeFilterOptions = arrayListOf<String>("special diets","pescetarian", "lacto vegetarian", "ovo vegetarian", "vegan", "vegetarian")

    val arrayOfIntolerance = arrayListOf<IntoleraceDataType>(IntoleraceDataType("Food Allergens:"),IntoleraceDataType("dairy"), IntoleraceDataType("egg"), IntoleraceDataType("gluten"),
        IntoleraceDataType("peanut"), IntoleraceDataType("sesame"), IntoleraceDataType("seafood"),
        IntoleraceDataType("shellfish"), IntoleraceDataType("soy"), IntoleraceDataType("sulfite"),
        IntoleraceDataType("tree nut"), IntoleraceDataType("wheat"))

    val selectedFilter = MutableStateFlow("")
    var selectedIntolerance : String = ""
    //input is list of names i.e {"Snapple Apple flavored drink 4oz","Mott's Apple pudding 3oz"}
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun detectFoodInText(listName: List<String>) {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                _viewVisibilityFlag.value = true
                try {
                    for (i in listName) {
                        //Note: For a post request, you can either provide a request body, passing in your text to the request body or you
                        //can provide a text parameter within the suspend function in your API Interface.
                        selectedProductName.value = URLEncoder.encode(i, "utf-8").toString()
                        nutritionApi.setBody()
                        Log.i("testURLEncoded: ", selectedProductName.value.toString())
                        val listOfIngredients: PostRequestResultWrapper =
                            nutritionApi.nutritionServicePost.detectFoodInText(
                            )
                        for (g in listOfIngredients.annotations) {
                            Log.i("testURLAnnotation", g.annotation)
                            foodInText.add(g.annotation)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    if (!isOnline(app)) {
                        displayToast("Not connected to internet")
                    }
                    Log.i("Exception", "$e")
                }

                if (!foodInText.isEmpty()) {

                    //listOfIngredientsString.value = foodInText.joinToString(separator = ",")

                    _navigatorFlag.value = true
                }
                _viewVisibilityFlag.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun findRecipeByIngredients() {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                _viewVisibilityFlag.value = true
                try {
                    Log.i("test","selectedFilter ${selectedFilter.value}")
                    Log.i("test","intolerance: ${selectedIntolerance}")
                    Log.i("test","foodInText: ${foodInText.joinToString(separator = ",").replace("[","")
                        .replace("]","")}")
                    val tempSelectedFilter = selectedFilter.value
                    val tempFoodInText =  foodInText.joinToString(separator = ",").replace("[","")
                        .replace("]","")
                    val resultWrapper: List<RecipeIngredientResult> =
                        nutritionApi.nutritionService.findByIngredients(tempSelectedFilter,selectedIntolerance,
                        tempFoodInText, true).results

                    Log.i("test","size test: ${resultWrapper.size}")
                    Log.i("test","[0]: ${resultWrapper[0].title}")
                    Timber.i("recipe list size: " + resultWrapper.size)
                    Timber.i("recipe[0]: " + resultWrapper[0].title)
                    /*for (i in resultWrapper) {
                        listOfRecipes.add(i)
                    }*/
                    _listOfRecipesLiveData.value = resultWrapper
                } catch (e: java.lang.Exception) {
                    if (!isOnline(app)) {
                        displayToast("Not connected to internet")
                    }
                    displayToast("Cannot find recipes with given ingredients")
                    Log.i("test",e.toString())
                    Timber.i(e)
                }
                _viewVisibilityFlag.value = false
            }
        }
    }

    //source: https://stackoverflow.com/questions/51141970/check-internet-connectivity-android-in-kotlin
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Log.i("test", ">M")
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }

        return false
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun loadIngredientListByNetwork() {
        wrapEspressoIdlingResource {
            Log.i("viewModel","load called")
            viewModelScope.launch {
                _viewVisibilityFlag.value = (true)
                if (searchItem.value != null) {
                    try {
                        val result: wrapperIngredientListNetworkDataClass =
                            nutritionApi.nutritionService.getIngredients(searchItem.value!!)
                        Log.i("test", "search item: ${searchItem.value}")
                        Log.i("test1", "Total products: ${result}")

                        _mutableLiveDataList.value = result.toDomainType()

                        _networkResultStateFlow.value = result.toDomainType()
                        Log.i("viewModelMutable", networkResultStateFlow.value!![0].name)

                        Toast.makeText(
                            app,
                            "Success",
                            Toast.LENGTH_SHORT
                        ).show()

                        val tempBool = false
                        _shoppingCartVisibilityFlag.postValue(tempBool)

                    } catch (e: Exception) {

                        if (!isOnline(app)) {
                            displayToast("Not connected to internet")
                        }
                        println("Error: ${e.message}")
                    }
                }
                _viewVisibilityFlag.value = (false)
            }
        }
    }

    fun displayToast(mString: String) {
        Toast.makeText(
            app,
            mString,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun saveRecipeNotification(recipeNotificationClassDomain: RecipeNotificationClassDomain) {
        viewModelScope.launch {
            ingredientRepository.saveNotificationRecipe(
                RecipeNotificationClassDomain(
                    recipeName = recipeNotificationClassDomain.recipeName,
                    missingIngredients = recipeNotificationClassDomain.missingIngredients,
                    mId = recipeNotificationClassDomain.mId
                )
            )
            _saveRecipeNotificationFlag.value = true
        }
    }

    val mFlag = MutableLiveData(false)
    fun getRecipeInstructions() {
        val listOfIngredientNameInInstruction = mutableListOf<String>()

        val listOfSteps = mutableListOf<String>()

        wrapEspressoIdlingResource {
            viewModelScope.launch {

                //Note: So the issue here was that the coroutine has not finished running. The solution was to make the network request function a
                // regular function to make it blocking Since the network function was initially a suspend function, the rest of the code was
                // proceeding under the assumption that "resultInstructions" was null. Even if "resultInstructions" returns a value the rest
                // of the code logic had already ran. So the solution was to make "resultInstructions" blocking.

                //Coroutines execute synchronously so the CoroutineScope of the Main thread will ensure that the job in Dispatchers.IO is
                //finished first before proceeding.
                //avoids NetworkOnMainThread exception error by running on a non-Main thread
                //Q: Why is this working but not the previous network request format?
                val networkResult: List<RecipeInstruction>? =
                    _navigateToRecipe.value?.id?.let {
                        nutritionApi.nutritionService.getRecipeInstructions(it.toString(), false)
                    }

                //iterates over each sub recipe i.e recipe for cake and recipe for frosting
                if (!networkResult.isNullOrEmpty()) {
                    for (i in networkResult) {
                        //adds title of sub recipes i.e frosting recipe in a cake recipe
                        if (i.name?.length!! > 0)
                            listOfSteps.add(i.name)

                        //iterates over "RecipeInstruction" to add the instructions steps into a list
                        for (steps in i.steps!!) {
                            //collects the ingredients mentioned in the recipe instructions
                            for (name in steps.ingredients!!) {
                                if (!name.name.isNullOrEmpty()) {
                                    listOfIngredientNameInInstruction.add(name.name)
                                }
                            }
                            steps.step?.let { listOfSteps.add(it) }
                        }
                    }

                    _listOfStepsLiveData.value = listOfSteps
                    val removeDuplicates = listOfIngredientNameInInstruction.toSet().toList()
                    _missingIngredients.value = removeDuplicates.minus(
                        foodInText)
                    mFlag.value = true
                }
            }
        }
    }

    fun saveIngredientItem() {
        if (selectedIngredient.value != null && selectedIngredient.value?.quantity!! >= 0) {
            wrapEspressoIdlingResource {
                viewModelScope.launch {
                    selectedIngredient.value?.let { ingredientRepository.saveNewIngredient(it) }
                }
            }
        } else
            Toast.makeText(app, "Invalid quantity", Toast.LENGTH_SHORT).show()
    }

    fun getLocalIngredientList() { //need DAO and repository
        Log.i("test", "getLocalList called")
        wrapEspressoIdlingResource {
            viewModelScope.launch {

                val ingredientResult: Result<List<IngredientDataClassDTO>> =
                    ingredientRepository.getIngredients()
                when (ingredientResult) {
                    is Result.Success<*> -> {
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
                        _listOfSavedIngredients.value = (dataList)
                    }
                    is Result.Error -> {
                        Log.i("test", "empty repository")
//                Toast.makeText(ApplicationProvider.getApplicationContext(),"${ingredientResult.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun clearRecipeNotificationTable() {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                ingredientRepository.clearNotificationRecipe()
            }
        }
    }

    @Override
    override fun onCleared() {
        super.onCleared()
    }
}

