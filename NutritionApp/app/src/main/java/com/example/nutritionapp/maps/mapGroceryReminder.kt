package com.example.nutritionapp.maps

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.MapGroceryReminderBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.example.nutritionapp.notification.NotificationDescriptionActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.lang.StringBuilder

class mapGroceryReminder : Fragment() {
private lateinit var binding : MapGroceryReminderBinding
    private var counter = 0
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var contxt: Context
    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private val GEOFENCE_RADIUS_IN_METERS = 1600F

    private var mNoneRecipeClass : NoneRecipeClass? = null
    private var recipeNotificationClass : RecipeNotificationClass? = null

    //Get the view model this time as a single to be shared with the another fragment, note the "override" tag
    //Note: We don't use "override val _viewModel: SaveReminderViewModel = get<SaveReminderViewModel>()"
    //because we are setting up our code in a fragment, if it was in an activity it would be allowed
    //https://stackoverflow.com/questions/53332832/unresolved-reference-none-of-the-following-candidates-is-applicable-because-of
     val _viewModel: IngredientViewModel by inject()
    //alternatively:
    //override val _viewModel by sharedViewModel<SaveReminderViewModel>()

    private var latLng: LatLng? = LatLng(33.0, -118.1)
    private lateinit var geofencingClient: GeofencingClient
    private var intent = Intent()

    private val geofencePendingIntent: PendingIntent by lazy {
        //intent = Intent(contxt, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        //intent.putExtra()
        PendingIntent.getBroadcast(contxt, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private lateinit var requestLocationSetting : ActivityResultLauncher<IntentSenderRequest>
    private lateinit var permissionCallback : ActivityResultLauncher<Array<String>>
    //ensures that permission is granted before asking to turn on location
    private var backgroundFlag = false
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.map_grocery_reminder,
            container,
            false
        )

        //Q: Culprit?
        //references the fragment instead of activity
        binding.lifecycleOwner = (contxt as Activity) as LifecycleOwner

        binding.viewModel = _viewModel

        geofencingClient = LocationServices.getGeofencingClient(contxt)
        //TODO: Strange, pressing add button after save button calls onCreateView

        //if coming from a selected Recipe then autofill the list with missing ingredients
        binding.missingIngredients.let {
            if (_viewModel.comingFromRecipeFlag.value == true)
            {
                it.setText(convertListStringToBulletedList(_viewModel.missingIngredients))
            }
            else
                it.setText("")
        }

        requestLocationSetting  = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                checkDeviceLocationSettingsAndStartGeofence()
            }
            else
            {
                //source: https://stackoverflow.com/questions/30729312/how-to-dismiss-a-snackbar-using-its-own-action-button
                val mSnackbar = Snackbar.make(
                    binding.root,
                    R.string.location_tracker_needed, Snackbar.LENGTH_LONG
                )
                mSnackbar.setAction("Dismiss"){mSnackbar.dismiss()}
                mSnackbar.show()
                Log.i("Test", "location setting denied access")
            }
        }

        val test = ActivityResultContracts.RequestMultiplePermissions()
        //TODO: Receiving Type Mismatch error in defining permissionCallback when
        // following: https://developer.android.com/training/permissions/requesting#allow-system-manage-request-code
        permissionCallback = registerForActivityResult(test) { permissions: Map<String, Boolean> ->

            //On Android <= 9, being granted location permission also grants background permission
            if(runningQOrLater && ActivityCompat.checkSelfPermission(
                    contxt,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED )
            {
                Log.i("test","Background permission is not granted")
                requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            else {
                checkDeviceLocationSettingsAndStartGeofence()
                Log.i("test", "permission granted contract, running less than Q")
            }
        }
        return binding.root
    }

    //onAttach is a callback that is called when the fragment is attached to its host activity
    //"context" refers to the Activity that the fragment is attached to
    //Note: I was receiving the "Not attached to an activity" error b/c the activity had not been created by the time
    //the fragment was created and initialized. This is why we use "contxt" instead of "requireContext()" since
    // "requireContext" will not have yet been initialized and is defaulted to "IllegalStateException"
    //Source: https://stackoverflow.com/questions/33742646/what-causes-a-fragment-to-get-detached-from-an-activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        contxt = context
    }

    /*
    According to Fragment lifecycle in Android OS, you cannot get the Activity associated with the fragment in the onCreateView,
    because the Activity with which the Fragment is associated will not be created at that stage.
     */

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        binding.selectLocation.setOnClickListener {
            //Navigate to another fragment to get the user location
            findNavController().navigate(mapGroceryReminderDirections.actionMapGroceryReminderToMapFragment())
        }

        binding.saveReminder.setOnClickListener {
            val bundle = Bundle()
            //TODO: Can potentially just use one class
            if (_viewModel.comingFromRecipeFlag.value == true) {
                recipeNotificationClass = RecipeNotificationClass(_viewModel.navigateToRecipe.value?.title!!, _viewModel.missingIngredients.value.toString())
                bundle.putSerializable("RecipeNotificationClass", recipeNotificationClass)
            }
            else
            {
                recipeNotificationClass = RecipeNotificationClass("Recipe",binding.missingIngredients.toString())
                bundle.putSerializable("RecipeNotificationClass", recipeNotificationClass)
            }
            intent.putExtras(bundle)
            checkPermission()
        }
    }

    /**
     * Starts the permission check and Geofence process only if the Geofence associated with the
     * current hint isn't yet active.
     */

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(contxt)

        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    //"exception" is defined in terms of "locationSettingsResponseTask". exception.resolution a placeholder for a pendingIntent
                    //source: https://knowledge.udacity.com/questions/650170#650189
                    if (backgroundFlag) {
                        Log.i("test","location settings need not be called")
                        backgroundFlag = false
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(exception.resolution).build()
                        requestLocationSetting.launch(intentSenderRequest)
                    }
                    return@addOnFailureListener
                }
                catch (sendEx: IntentSender.SendIntentException) {
                    Timber.i("Error getting location settings resolution:" + sendEx.message)
                    //Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            }
            else {
                Snackbar.make(
                    binding.root,
                   R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful && !isDetached) {
                addGeofenceForClue()
            }
        }
    }



    //call only once permission is granted
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addGeofenceForClue() {
        //no need for check on running Q or later since
        // "The background location permission is needed in SaveReminderFragment since we want to kick off a Geofence." - reviewer
        // Build the Geofence Object
        val geofence = latLng?.latitude?.let {
            latLng?.longitude?.let { it1 ->
                Geofence.Builder()
                    // Set the request ID, string to identify the geofence. Depends on whether we are selecting a recipe or a non-recipe
                    .setRequestId(recipeNotificationClass?.id) //reminderDataItem.id)//_viewModel.latLng.value?.latitude.toString())
                    // Set the circular region of this geofence.
                    .setCircularRegion(
                        it,
                        it1,
                        GEOFENCE_RADIUS_IN_METERS)
                    // Set the expiration duration of the geofence. This geofence gets
                    // automatically removed after this period of time.
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build()
            }
        }

        //Build the geofence request
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        if ((ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)

        ) {
            Log.i("test","foreground permission not granted in addGeofence()")
        }


        //Toast.makeText(contxt,"Permission Granted",Toast.LENGTH_SHORT).show()

        //to add a geofence, you add the actual geofence location (geofenceRequest) as well as where you want the
        //activity to start once the geofence is triggered (geofencePendingIntent), which in our case is BroadcastReceiver
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
            addOnSuccessListener {

                Toast.makeText(contxt, "Succesfully added geofence", Toast.LENGTH_SHORT).show()
                Log.i("test", "added geofence")
                //findNavController().navigate(mapGroceryReminderDirections)
            }
            addOnFailureListener {
                Toast.makeText(
                    contxt, R.string.geofences_not_added,
                    Toast.LENGTH_SHORT
                ).show()
                if ((it.message != null)) {
                    Log.w("test", it.message!!)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkPermission() : Boolean
    {
        if(!binding.missingIngredients.text.isEmpty()) {
            Toast.makeText(contxt, "Missing information", Toast.LENGTH_SHORT).show()
            return false
        }

        if (runningQOrLater) {
            Log.i("test","runningQOrLater")
            requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            return false
        }

        if ((ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {

            val permissionObject = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            permissionCallback.launch(permissionObject)
            return false
        }
        return true
    }

    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "RemindersActivity.savereminder.action.ACTION_GEOFENCE_EVENT"
    }
    //inspired by: https://github.com/yuriikonovalov/UdacityNanodegreeLocationReminderApp/blob/master/app/src/main/java/com/udacity/project4/locationreminders/savereminder/SaveReminderFragment.kt
    @RequiresApi(Build.VERSION_CODES.Q)
    private var requestBackgroundLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (permissionGranted) {
            backgroundFlag = true
            checkDeviceLocationSettingsAndStartGeofence()
        }
        else
            Toast.makeText(contxt,"background permission needed for LocationReminder", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("test, is finishing?: " + requireActivity().isFinishing)
        //make sure to clear the view model after destroy, as it's a single view model.
       // _viewModel.onClear()
    }

    fun convertListStringToBulletedList( mList : LiveData<List<String>>) : String
    {
        val sb = StringBuilder()
        mList.value.let {
            if (!it.isNullOrEmpty()) {
                for ( item in it)
                {
                    sb.append("• $item")
                }
            }
            else
                sb.append("")
        }
        return sb.toString()
    }
}