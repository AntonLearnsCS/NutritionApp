package com.example.nutritionapp.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

//Note: Broadcast Receivers must be specified in the manifest

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    //TODO: implement the onReceive method to receive the geofencing events at the background
    override fun onReceive(context: Context, intent: Intent) {

        //TODO: Remove Geofence after user clicks notification button or will it expire once enter is triggered?

        //source: https://stackoverflow.com/questions/47593205/how-to-pass-custom-object-via-intent-in-kotlin
        Log.i("test","Broadcast Received")
        Toast.makeText(context,"Broadcast Received", Toast.LENGTH_LONG).show()
        GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
    }
}