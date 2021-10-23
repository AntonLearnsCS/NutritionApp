package com.example.nutritionapp.ingredientlist

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
//import androidx.test.core.app.ApplicationProvider
import com.example.nutritionapp.database.IngredientDataClass
import kotlinx.coroutines.launch
import com.example.nutritionapp.util.Result
import com.example.nutritionapp.database.IngredientDataSourceInterface
import com.example.nutritionapp.database.dto.IngredientDataClassDTO
import com.example.nutritionapp.ingredientlist.testNutritionApi.nutritionServicePost
import com.example.nutritionapp.network.*
import com.example.nutritionapp.recipe.PostRequestResultWrapper
import com.example.nutritionapp.recipe.RecipeIngredientResult
import com.example.nutritionapp.util.wrapEspressoIdlingResource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.*
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
    val ingredientRepository: IngredientDataSourceInterface
) : AndroidViewModel(app), Observable {

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
        get() = this._viewVisibilityFlag

    fun setViewVisibilityFlag(boolean: Boolean) {
        _viewVisibilityFlag.value = boolean
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

    val listOfRecipes = mutableListOf<RecipeIngredientResult>()

    var listOfNetworkRequestedIngredients: List<IngredientDataClass>? = null

    //Note: Don't set the MutableLiveData to null, b/c technically it is not initialized so any assignment will not change the null value
    //and variable observing this MutableLiveData will return null
    var mutableLiveDataList: MutableLiveData<List<IngredientDataClass>> = MutableLiveData()
    var listOfSavedIngredients: MutableLiveData<List<IngredientDataClass>> =
        MutableLiveData()//null//getLocalIngredientList()

    //two-way binding
    //no need to add "?query=" since the getIngredients() of the IngredientsApiInterface will do that
    var searchItem = MutableLiveData<String>()

    val selectedIngredient = MutableLiveData<IngredientDataClass>()
    val foodInText = mutableListOf<String>()
    val listOfIngredientsString = MutableLiveData<String>()


    //input is list of names i.e {"Snapple Apple flavored drink 4oz","Mott's Apple pudding 3oz"}
    fun detectFoodInText(listName: List<String>) {
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

    fun findRecipeByIngredients() {
        viewModelScope.launch {
            this@IngredientViewModel._viewVisibilityFlag.value = true
            try {
                val resultWrapper: List<RecipeIngredientResult> =
                    NutritionAPI.nutritionServiceGetRecipeIngredients.findByIngredients(
                        listOfIngredientsString.value!!
                    )
                Log.i("test", "recipe list size: ${resultWrapper.size}")
                Log.i("test", "recipe[0]: ${resultWrapper[0].title}")
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

    fun loadIngredientListByNetwork() {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                _viewVisibilityFlag.value = true
                if (searchItem.value != null) {
                    try {
                        val result: wrapperIngredientListNetworkDataClass =
                            NutritionAPI.nutritionService.getIngredients(searchItem.value!!)
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
                this@IngredientViewModel._viewVisibilityFlag.value = false
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

    private val _navigateToDetail = MutableLiveData<IngredientDataClass>()
    val navigateToDetail: LiveData<IngredientDataClass>
        get() = _navigateToDetail

    fun setNavigateToDetail(ingredientItem: IngredientDataClass) {
        _navigateToDetail.value = ingredientItem
    }

    fun setNavigateToDetailNull() {
        _navigateToDetail.value = null
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
val mediaType = MediaType.parse("application/x-www-form-urlencoded")

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

//I%20like%20to%20eat%20delicious%20tacos.%20Only%20cheeseburger%20with%20cheddar%20are%20better%20than%20that.%20But%20then%20again%2C%20pizza%20with%20pepperoni%2C%20mushrooms%2C%20and%20tomatoes%20is%20so%20good!"
//Note: Need to add MediaType
val clientPostRequest by lazy {
    OkHttpClient.Builder().addInterceptor(object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response? {
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

object testNutritionApi {
    val nutritionServicePost: IngredientsApiInterfacePost by lazy {
        retrofitPost.create(
            IngredientsApiInterfacePost::class.java
        )
    }
}