package com.example.nutritionapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

private const val BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com"


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
var client = OkHttpClient.Builder().addInterceptor(object : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "743dd97869msh559abee3f899bd4p131dd1jsn866e00036c54")
            .build()
        return chain.proceed(newRequest)
    }
}).build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

/**
 * A public interface that exposes the [getProperties] method
 */
interface IngredientsApiInterface {
    /**
     * Returns a Coroutine [List] of [MarsProperty] which can be fetched with await() if in a Coroutine scope.
     * The @GET annotation indicates that the "realestate" endpoint will be requested with the GET
     * HTTP method
     */

    @GET("search")
    suspend fun getProperties(@Query("searchFilter") type: String): List<IngredientListNetworkDataClass>
}

//we want to expose the retrofit instance because creating a Retrofit instance is expensive
object NutritionAPI {
    val nutritionService : IngredientsApiInterface by lazy { retrofit.create(IngredientsApiInterface::class.java) }
}

/*

val client = OkHttpClient()

val request = Request.Builder()
    .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/food/products/search?query=carrot&maxCalories=5000&minProtein=0&maxProtein=100&minFat=0&maxFat=100&minCarbs=0&maxCarbs=100&minCalories=0&offset=0&number=10")
    .get()
    .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
    .addHeader("x-rapidapi-key", "743dd97869msh559abee3f899bd4p131dd1jsn866e00036c54")
    .build()

val response = client.newCall(request).execute()*/