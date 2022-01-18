package com.example.nutritionapp.network

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.example.nutritionapp.ingredientlist.selectedProductName
import com.example.nutritionapp.recipe.PostRequestResultWrapper
import com.example.nutritionapp.recipe.RecipeIngredientResult
import com.example.nutritionapp.recipe.RecipeIngredientResultWrapper
import com.example.nutritionapp.recipe.RecipeInstruction
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.io.IOException

private const val BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/"

open class mNutritionApi {


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
    /**
     * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
     * full Kotlin compatibility.
     */

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

/*
An interceptor will intercept the HTTP request and append the headers when Retrofit is initialized
so that we don't have to append the headers for every call
source: https://stackoverflow.com/questions/41078866/retrofit2-authorization-global-interceptor-for-access-token/41082979#41082979
source: https://stackoverflow.com/questions/42491733/passing-api-key-in-retrofit-2-android-studio

 Q: Why do we add a "Header"?
 A: A request header is an HTTP header that can be used in an HTTP request to provide information about the request context,
 so that the server can tailor the response (source:https://developer.mozilla.org/en-US/docs/Glossary/Request_header)
 */


    //Separate client and Retrofit object for @GET and @POST requests since they have different headers
//                .addHeader("content-type", "application/json")


    var clientGetRequest = OkHttpClient.Builder().addInterceptor(object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

            val newRequest: Request = chain.request().newBuilder()
                .get()
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .addHeader(
                    "x-rapidapi-key",
                    "743dd97869msh559abee3f899bd4p131dd1jsn866e00036c54"
                )//Error: HTTP 401 Unauthorize
                .build()

            return chain.proceed(newRequest)
        }

    }).build()

      val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(clientGetRequest)
        .build()

    val nutritionService: IngredientsApiInterface by lazy {
        retrofit.create(IngredientsApiInterface::class.java)
    }

    val nutritionServicePost: IngredientsApiInterface by lazy {
        retrofitPost.create(
            IngredientsApiInterface::class.java
        )
    }
}
    /**
     * A public interface that exposes the [getIngredients] method
     */
    interface IngredientsApiInterface {
        /**
         * Returns a Coroutine [List] of [Ingredients] which can be fetched with await() if in a Coroutine scope.
         * The @GET annotation indicates that the "realestate" endpoint will be requested with the GET
         * HTTP method
         */

        //Note: "query" is what will be appended to the end point i.e http.somesite.com/search?query=
        @GET("food/products/search")
        suspend fun getIngredients(@Query("query") type: String): wrapperIngredientListNetworkDataClass

        //diet={diet}&intolerance={intolerance}&includeIngredients={includeIngredients}")
        @GET("recipes/complexSearch")
        suspend fun findByIngredients(@Query("diet") diet : String, @Query("intolerances") intolerance : String,
                                      @Query("includeIngredients") includeIngredients : String, @Query("instructionsRequired") instructionsRequired : Boolean): RecipeIngredientResultWrapper

        @GET("recipes/findByIngredients")
        suspend fun findByIngredientsOriginal(@Query("ingredients") ingredients : String) : List<RecipeIngredientResult>

        //run as blocking function by omitting suspend modifier
        @GET("recipes/{id}/analyzedInstructions")
        suspend fun getRecipeInstructions(@Path("id") id : String, @Query("stepBreakdown") boolean: Boolean) : List<RecipeInstruction>

        @POST("food/detect")
        suspend fun detectFoodInText(): PostRequestResultWrapper
    }

