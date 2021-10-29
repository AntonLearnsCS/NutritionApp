package com.example.nutritionapp.maps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.BuildConfig
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.MapBinding
import com.example.nutritionapp.ingredientlist.IngredientViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class mapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: MapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map : GoogleMap
    private lateinit var contxt: Context
    private var latitude : Double = 33.8447593
    private var longitude : Double = -118.1480706
    private var defaultLocation = LatLng(latitude,longitude)
    private val zoomLevel = 12f
    private lateinit var permissionCallback : ActivityResultLauncher<Array<String>>
    private var backgroundFlag = false
    private var locationFlag = false
    private lateinit var requestLocationSetting : ActivityResultLauncher<IntentSenderRequest>
    private val viewModel: IngredientViewModel by inject()
    //TODO: Get API KEY
    //val apikey = BuildConfig.mMaps_API_KEY

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.map,container,false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.lifecycleOwner = requireActivity()

        setHasOptionsMenu(true)

        requestLocationSetting  = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i("test","location setting enabled")

                locationFlag = true
                getDeviceLocation()
            }
            else
            {
                //source: https://stackoverflow.com/questions/30729312/how-to-dismiss-a-snackbar-using-its-own-action-button
                val mSnackbar = Snackbar.make(
                    binding.layout,
                    R.string.location_tracker_needed, Snackbar.LENGTH_LONG
                )
                mSnackbar.setAction("Dismiss"){mSnackbar.dismiss()}
                mSnackbar.show()
                Log.i("Test", "location setting denied access")
            }
        }

        val permissionArrayRequest = ActivityResultContracts.RequestMultiplePermissions()

        permissionCallback = registerForActivityResult(permissionArrayRequest) { permissions: Map<String, Boolean> ->
            if(permissions.containsValue(true))
            {
                if (ActivityCompat.checkSelfPermission(
                        contxt,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        contxt,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@registerForActivityResult
                }
                map.setMyLocationEnabled(true)

                //map.isMyLocationEnabled = true
                backgroundFlag = true
                checkDeviceLocationSettings()
                Log.i("test", "permission granted contract")
            }
            else
            {
                //if user denies and dismisses future permission requests
                if((shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == false
                            || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) == false))
                {
                    val mSnackbar = Snackbar.make(
                        binding.layout,
                        "Go to app settings to enable map location", Snackbar.LENGTH_SHORT
                    )

                    mSnackbar.setAction("dismiss"){mSnackbar.dismiss()}
                    mSnackbar.show()

                }
                val mSnackbar = Snackbar.make(
                    binding.layout,
                    "Enabling location moves map to your location", Snackbar.LENGTH_LONG
                )

                mSnackbar.setAction("dismiss"){mSnackbar.dismiss()}
                mSnackbar.show()

                Log.i("test", "permission not granted contract")
            }
        }

        enableLocation()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //define your own context since we want to ensure that the context we use refers to the state after the fragment
        //has been attached to the activity; this avoids the "Fragment not attached to activity" error
        contxt = context
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap!= null)
            map = googleMap

        getDeviceLocation()

        map.addMarker(MarkerOptions().position(defaultLocation))
        setPoiClick(map)
        setMapLongClick(map)

        if (ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //Note: setMyLocationEnabled updates user icon on map
            map.setMyLocationEnabled(true)

            //map.isMyLocationEnabled = true
        }
    }

    fun locationPermissionGranted() : Boolean
    {
        if ((ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    )
        ){
            return true
        }
        else
            return false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
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
                        backgroundFlag = false
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(exception.resolution).build()
                        requestLocationSetting.launch(intentSenderRequest)
                    }
                }
                catch (sendEx: IntentSender.SendIntentException) {
                    Timber.i("Error getting location settings resolution:" + sendEx.message)
                }
            }
            else {
                Snackbar.make(
                    binding.layout,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful && !isDetached) {
                if (ActivityCompat.checkSelfPermission(
                        contxt,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        contxt,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@addOnCompleteListener
                }
                map.setMyLocationEnabled(true)

                //map.isMyLocationEnabled = true
                Log.i("test","location settings is enabled")

            }
        }
    }

    //source: https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial#kotlin_7
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        //TODO: By creating the instance of fusedLocationProviderClient here in addition to onCreate, the
        //FusedLocationProviderClient - manages the underlying location technologies, such as GPS and Wi-Fi,
        // and provides a simple API so that you can specify requirements at a high level, like high accuracy or low power.

        val fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())
        var lastKnownLocation: Location
        try {
            if (locationPermissionGranted()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful && task.result != null) {
                        //TODO: Why was task.result null?
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        defaultLocation =
                            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation.latitude,
                                    lastKnownLocation.longitude
                                ), zoomLevel
                            )
                        )
                    }
                    else {
                        requestLocation()
                        Log.i("test", "Current location is null. Using defaults.")
                    }
                }
            }
            else
            {
                Log.i("test", "Current location is null. Using defaults.")
                map.moveCamera(
                    CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, zoomLevel))
                //map.uiSettings?.isMyLocationButtonEnabled = false
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    fun enableLocation()
    {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
        {
            Log.i("test", "SelectLocation foreground permission enabled")
            return
        }
        else{
            Log.i("test", "SelectLocation foreground permission not yet enabled")

            val mArray = arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            permissionCallback.launch(mArray)
        }
    }

    //https://stackoverflow.com/questions/63223410/does-fusedlocationproviderclient-need-to-initialize-location-often-null
    private fun requestLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2000
        locationRequest.numUpdates = 2
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(contxt)
        if (ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Log.i("test","requestLocation called")
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
    }
    private fun setMapLongClick(map: GoogleMap) {

        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(), //A object represents a specific geographical, political, or cultural region
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            /*
            As you can see, the problem isn't with MutableLiveData, but with your ViewModel. Since it's a SharedViewModel,
            you need to have it's LifeCycleOwner set to either an Activity or to your Application class. This way, if you
            reuse that LifeCycleOwner with that ViewModel, the changes of your LiveData properties will be visible to your
             other Activities or Fragment
             Source: https://stackoverflow.com/questions/54871649/mutablelivedata-sets-value-but-getvalue-returns-null
             */


            println("SelectLocation: " + latLng.latitude.toString() + ", " + latLng.longitude.toString())
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener {
                poi ->

            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            p0 ?: return
            super.onLocationResult(p0)
            for (location in p0.locations)
            {
                if (location != null) {
                    val tempLatLng = LatLng(location.latitude, location.longitude)
                    defaultLocation = tempLatLng
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), zoomLevel
                        )
                    )
                    if (ActivityCompat.checkSelfPermission(
                            contxt,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            contxt,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    map.setMyLocationEnabled(true)

                    //map.isMyLocationEnabled = true
                }
            }
        }
    }


}