package com.example.nutritionapp.ingredientlist

//import androidx.test.core.app.ApplicationProvider
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.IngredientRepository
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.ingredientlist.testNutritionApi.nutritionServicePost
import com.example.nutritionapp.maps.RecipeNotificationClass
import com.example.nutritionapp.network.*
import com.example.nutritionapp.recipe.*
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Call
import retrofit2.Response

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import java.io.IOException
import java.net.URLEncoder

/*
To get context inside a ViewModel we can either extend AndroidViewModel. Do not use ApplicationProvider in production code, only in tests
https://stackoverflow.com/questions/51451819/how-to-get-context-in-android-mvvm-viewmodel
 */

val selectedProductName = MutableLiveData<String>("Apple,flour,sugar")

class IngredientViewModel(
    val app: Application,
    val ingredientRepository: IngredientDataSourceInterface, val nutritionApi : mNutritionApi
) : AndroidViewModel(app), Observable {

    val nutritionService: IngredientsApiInterface by lazy {
        NutritionAPI.retrofit.create(IngredientsApiInterface::class.java)
    }

    //flag for shopping cart image
    private val _shoppingCartVisibilityFlag = MutableLiveData<Boolean>(true)
    val shoppingCartVisibilityFlag: LiveData<Boolean>
        get() = _shoppingCartVisibilityFlag
/*
    fun setShoppingCartVisibilityFlagTrue()
    {
        _shoppingCartVisibilityFlag.value = true
    }
    fun setShoppingCartVisibilityFlagFalse()
    {
        _shoppingCartVisibilityFlag.value = false
    }*/

    //flag for navigating once coroutine involving search recipe button in ingredientListFragment is finished
    private val _navigatorFlag = MutableLiveData<Boolean>(false)
    val navigatorFlag: LiveData<Boolean>
        get() = _navigatorFlag

    fun setNavigatorFlag(boolean: Boolean) {
        _navigatorFlag.value = boolean
    }

    private val _viewVisibilityFlag = MutableLiveData<Boolean>(false)
    val viewVisibilityFlag: LiveData<Boolean>
        get() = _viewVisibilityFlag


    private val _latLng = MutableLiveData<LatLng>()
    val latLng : LiveData<LatLng>
    get() = _latLng

    fun setLatLng(latLng: LatLng)
    {
        _latLng.value = latLng
    }
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


    private val _listOfStepsLiveData = MutableLiveData<List<String>>()
    val listOfStepsLiveData: LiveData<List<String>>
        get() = _listOfStepsLiveData


    var listOfNetworkRequestedIngredients: List<IngredientDataClass>? = null

    //Note: Don't set the MutableLiveData to null, b/c technically it is not initialized so any assignment will not change the null value
    //and variable observing this MutableLiveData will return null
    var mutableLiveDataList: MutableLiveData<List<IngredientDataClass>> = MutableLiveData()
    var listOfSavedIngredients: MutableLiveData<List<IngredientDataClass>> = MutableLiveData()

    //two-way binding
    //no need to add "?query=" since the getIngredients() of the IngredientsApiInterface will do that
    var searchItem = MutableLiveData<String>()

    val selectedIngredient = MutableLiveData<IngredientDataClass>()
    val foodInText = mutableListOf<String>()


    val searchRecipeEditTextFlag = MutableLiveData(false)

    fun setSearchRecipeEditTextClear()
    {
        foodInText.clear()
    }



    val listOfIngredientsString = MutableLiveData<String>()

    //input is list of names i.e {"Snapple Apple flavored drink 4oz","Mott's Apple pudding 3oz"}
    fun detectFoodInText(listName: List<String>) {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                _viewVisibilityFlag.value = true
                try {
                    for (i in listName) {
                        //Note: For a post request, you can either provide a request body, passing in your text to the request body or you
                        //can provide a text parameter within the suspend function in your API Interface.
                        selectedProductName.value = URLEncoder.encode(i, "utf-8").toString()
                        setBody()
                        Log.i("testURLEncoded: ", selectedProductName.value.toString())
                        val listOfIngredients: PostRequestResultWrapper =
                            nutritionServicePost.detectFoodInText(
                            )
                        for (g in listOfIngredients.annotations) {
                            Log.i("testURLAnnotation", g.annotation)
                            foodInText.add(g.annotation)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Log.i("Exception", "$e")
                }

                if (!foodInText.isEmpty()) {
                    listOfIngredientsString.value = foodInText.joinToString(separator = ",")
                    _navigatorFlag.value = true
                }
                _viewVisibilityFlag.value = false
            }
        }
    }

    fun findRecipeByIngredients() {
        val listOfRecipes = mutableListOf<RecipeIngredientResult>()
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                _viewVisibilityFlag.value = true
                try {
                    val resultWrapper: List<RecipeIngredientResult> =
                        nutritionApi.nutritionService.findByIngredients(
                            listOfIngredientsString.value!!
                        )
                    Log.i("testRecipeById", "recipe list size: ${resultWrapper.size}")
                    Log.i("testRecipeById", "recipe[0]: ${resultWrapper[0].title}")
                    for (i in resultWrapper) {
                        listOfRecipes.add(i)
                    }

                    _listOfRecipesLiveData.value = listOfRecipes

                } catch (e: java.lang.Exception) {
                    Log.i("Exception", "$e")
                }
                _viewVisibilityFlag.value = false
            }
        }
    }

    fun loadIngredientListByNetwork() {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                _viewVisibilityFlag.value = true
                if (searchItem.value != null) {
                    try {
                        val result: wrapperIngredientListNetworkDataClass =
                            nutritionApi.nutritionService.getIngredients(searchItem.value!!)
                        Log.i("test", "search item: ${searchItem.value}")
                        println("Total products: ${result.totalProducts}")

                        listOfNetworkRequestedIngredients = result.products.map {
                            IngredientDataClass(
                                name = it.name, quantity = 1, id = it.id,
                                imageUrl = it.imageUrl, imageType = it.imageType
                            )
                        }

                        mutableLiveDataList.value = listOfNetworkRequestedIngredients

                        Toast.makeText(
                            app,
                            "networkRequestSuccess",
                            Toast.LENGTH_SHORT
                        ).show()

                        _shoppingCartVisibilityFlag.value = false
                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }

                } else {
                    Toast.makeText(
                        app,
                        "Missing search parameter",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                _viewVisibilityFlag.value = false
            }
        }
    }

    fun displayToast(mString: String)
    {
        Toast.makeText(
            app,
            "$mString",
            Toast.LENGTH_SHORT
        ).show()
    }
    private val _saveRecipeNotificationFlag = MutableLiveData(false)
    val saveRecipeNotificationFlag : LiveData<Boolean>
    get() = _saveRecipeNotificationFlag

    fun setSaveRecipeNotificationFlagBoolean(boolean: Boolean)
    {
        _saveRecipeNotificationFlag.value = boolean
    }

    fun saveRecipeNotification(recipeNotification: RecipeNotificationClass)
    {

        viewModelScope.launch {
            ingredientRepository.saveNotificationRecipe(recipeNotification)
            _saveRecipeNotificationFlag.value = true
        }
    }


    private val _missingIngredients = MutableLiveData<List<String>>()
    val missingIngredients : LiveData<List<String>>
    get() = _missingIngredients

    fun setMissingIngredientsNull()
    {
        _missingIngredients.value = null
    }

    val mFlag = MutableLiveData(false)
    fun getRecipeInstructions()
    {
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
                        nutritionApi.nutritionService.getRecipeInstructions(it, false)
                    }

                    Log.i("testFunctionID", "ID: ${_navigateToRecipe.value?.id}")

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

                        _missingIngredients.value = listOfIngredientNameInInstruction.minus(foodInText)

                        mFlag.value = true
                    }
            }
        }
    }

    fun saveIngredientItem() {
        if (selectedIngredient.value != null && selectedIngredient.value?.quantity!! > 0) {
            wrapEspressoIdlingResource {
                viewModelScope.launch {
                    selectedIngredient.value?.let { ingredientRepository.saveNewIngredient(it) }
                }
            }
        } else
            Toast.makeText(app, "Invalid quantity", Toast.LENGTH_SHORT).show()
    }

    fun getLocalIngredientList() { //need DAO and repository
        Log.i("test","getLocalList called")
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
                        listOfSavedIngredients?.value = dataList
                    }
                    is Result.Error -> {
                        Log.i("test", "empty repository")
//                Toast.makeText(ApplicationProvider.getApplicationContext(),"${ingredientResult.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun clearRecipeNotificationTable()
    {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                ingredientRepository.clearNotificationRecipe()
            }
        }
    }

    //Don't need to save noneRecipe and Recipe class to database since that information will be inserted into the pending intent and can be extracted from said intent without
    //accessing the database
/*
    fun saveNoneRecipe(noneRecipeClass: NoneRecipeClass)
    {
        viewModelScope.launch {
            ingredientRepository.saveNoneRecipe (noneRecipeClass)
        }
    }

    fun getNoneRecipeById (key : String)
    {
        viewModelScope.launch {
            val result = ingredientRepository.getNoneRecipeById(key)
        }

    }

    fun saveNotificationRecipe (recipeNotification : RecipeClassBroadcasts)
    {
        viewModelScope.launch {
            ingredientRepository.saveNotificationRecipe(recipeNotification)
        }
    }*/



    private val _navigateToDetail = MutableLiveData<IngredientDataClass>()
    val navigateToDetail: LiveData<IngredientDataClass>
        get() = _navigateToDetail

    fun setNavigateToDetail(ingredientItem: IngredientDataClass) {
        _navigateToDetail.value = ingredientItem
    }

    fun setNavigateToDetailNull() {
        _navigateToDetail.value = null
    }


    private var _navigateToRecipeFlag = MutableLiveData(false)
    val navigateToRecipeFlag : LiveData<Boolean>
    get() = _navigateToRecipeFlag


    private val _navigateToRecipe = MutableLiveData<RecipeIngredientResult>()
    val navigateToRecipe: LiveData<RecipeIngredientResult>
        get() = _navigateToRecipe

    fun setNavigateToRecipe(recipe: RecipeIngredientResult) {
        _navigateToRecipe.value = recipe }

    fun setNavigateToRecipeNull()
    {
        _navigateToRecipe.value = null
    }

    fun setNavigateToRecipeFlag(boolean: Boolean) {
        Log.i("test1","setNavigateToRecipeFlag called")
        _navigateToRecipeFlag.value = boolean
    }

    private val _quantityCounter = MutableLiveData<Int>(1)
    val quantityCounter : LiveData<Int>
    get() = _quantityCounter

    fun increaseQuantityCounter() {
        val temp = _quantityCounter.value?.plus(1)
        _quantityCounter.value = temp
        selectedIngredient.value?.quantity = temp!!
    }

    fun decreaseQuantityCounter()
    {
        Log.i("test","decrease button called, ")
        if (_quantityCounter.value!! > 0)
        {
            //note - we need to reassign the mutableLiveData to a new variable for observers to observe a change in value
                //you can't just update the existing value inside the mutableLiveData
            val temp = _quantityCounter.value!!.minus(1)
            _quantityCounter.value = temp
            selectedIngredient.value?.quantity = temp
        }

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

private const val BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


//----------------------------------------POST Request--------------------------------------------------------------------------------
val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()

//Q: What is the purpose of ".post(body)" in the OkHttpClient?
//A: To input your text that will be acted upon by the API POST function
var body: RequestBody? = null
fun setBody() {
    body = RequestBody.create(
        mediaType,
        "text=${selectedProductName.value}"
    )
    Log.i("testSetBody()", selectedProductName.value.toString())
}

//Note: Need to add MediaType
val clientPostRequest by lazy {
    OkHttpClient.Builder().addInterceptor(object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val newRequest: Request = chain.request().newBuilder()
                .post(body!!)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .addHeader(
                    "x-rapidapi-key",
                    "743dd97869msh559abee3f899bd4p131dd1jsn866e00036c54"
                )//Error: HTTP 401 Unauthorized
                .build()
            return chain.proceed(newRequest)
        }
    }).build()
}

private val retrofitPost by lazy {
    Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(clientPostRequest)
        .build()
}

interface IngredientsApiInterfacePost {
    @POST("food/detect")
    suspend fun detectFoodInText(): PostRequestResultWrapper
    //fun addUser(@Body userData: UserInfo): Call<UserInfo>
}

@Suppress("UNCHECKED_CAST")
class IngredientViewModelFactory (
    private val ingredientRepository: IngredientDataSourceInterface
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (IngredientViewModel(ApplicationProvider.getApplicationContext(), ingredientRepository, NutritionAPI) as T)
}

object testNutritionApi {
    val nutritionServicePost: IngredientsApiInterfacePost by lazy {
        retrofitPost.create(
            IngredientsApiInterfacePost::class.java
        )
    }
}