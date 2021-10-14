package com.example.nutritionapp.authentication


import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.LiveData
import com.example.nutritionapp.util.wrapEspressoIdlingResource

/**
 * This class observes the current FirebaseUser. If there is no logged in user, FirebaseUser will
 * be null.
 *
 * Note that onActive() and onInactive() will get triggered when the configuration changes (for
 * example when the device is rotated). This may be undesirable or expensive depending on the
 * nature of your LiveData object, but is okay for this purpose since we are only adding and
 * removing the authStateListener.
 */
class FirebaseUserLiveData : LiveData<FirebaseUser?>() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    // TODO set the value of this FireUserLiveData object by hooking it up to equal the value of the
    //  current FirebaseUser. You can utilize the FirebaseAuth.AuthStateListener callback to get
    //  updates on the current Firebase user logged into the app.
    //AuthStateListener is triggered whenever a user logs in or out
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuthListener ->
        //value here refers to the value of the whole class, which is of type LiveData
        value = firebaseAuthListener.currentUser
    }
    // TODO Use the FirebaseAuth instance instantiated at the beginning of the class to get an
    //  entry point into the Firebase Authentication SDK the app is using.
    //  With an instance of the FirebaseAuth class, you can now query for the current user.


    // When this object has an active observer, start observing the FirebaseAuth state to see if
    // there is currently a logged in user.
    override fun onActive() {
        wrapEspressoIdlingResource {
            firebaseAuth.addAuthStateListener(authStateListener)
        }
    }

    // When this object no longer has an active observer, stop observing the FirebaseAuth state to
    // prevent memory leaks.
    override fun onInactive() {
        wrapEspressoIdlingResource {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }
}