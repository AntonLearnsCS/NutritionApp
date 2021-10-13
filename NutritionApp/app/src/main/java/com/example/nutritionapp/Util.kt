package com.example.nutritionapp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class Util {
}
/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
//TODO: Where can I go to get more information on the use of this sealed class?
//So you have the option to use one of each data class when a data type is wrapped in Result<>
//"data" in: data class Success<out T : Any>(val data: T) : Result<T>() refers to any data type wrapped by Result. In the case of
//"Result<List<ReminderDTO>>", data refers to <List<ReminderDTO>>

//We are able to wrap Result around any data type b/c we specify the generic T as being of type "Any". So the "T" in "(val data: T)"
//can allows "data" to be any data type that extends (via keyword out) "Any". If we only wanted Result to encapsulate data types that
//extend "Number" then we could write: data class Success<out T : Number>(val data: T) : Result<T>() . Alternatively, if we want the
//opposite, meaning data types that are super to Integer i.e Number,
// we could do: data class Success<in T : Number>(val data: T) : Result<T>()

// "<out T : Any>" is the signature, so that when we use Result, we can create only objects that extend the
// "Any" i.e val mResult : Result<Any> = Result<listOf(1,2,3)>

//source: https://stackoverflow.com/questions/44298702/what-is-out-keyword-in-kotlin
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val message: String?, val statusCode: Int? = null) :
        Result<Nothing>()
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */

val Result<*>.succeeded
    get() = this is Result.Success




@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}