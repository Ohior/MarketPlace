package com.example.marketplace.tool

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import kotlin.math.*

class Locator(private val activity: Activity, private val context: Context) {
    val latitude_1 = 6.339185
    val longitude_1 = 5.617447
    val latitude_2 = 7.250771 //6.2059295 //6.749140
    val longitude_2 = 5.210266 //6.6958939 //6.073215

    fun requestLocationPermissions(permissionid:Int){
        //REQUEST FOR LOCATION PERMISSION
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), permissionid)
    }

    fun openLocationSetting(){
        // OPEN LOCATION SETTINGS
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }

    fun checkLocationPermission():Boolean {
        //CHECK FOR PERMISSION
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    fun haveLocationPermission():Boolean {
        //CHECK FOR PERMISSION
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    fun isLocationEnabled():Boolean {
        val locationmanager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    fun getDistanceBetweenTwoPosition(hlat: Double, hlong: Double, glat: Double, glong: Double): Double {
        val startPoint = Location("locationA")
        startPoint.latitude = hlat
        startPoint.longitude = hlong

        val endPoint = Location("locationB")
        endPoint.latitude = glat
        endPoint.longitude = glong
        return (startPoint.distanceTo(endPoint)/1000).toDouble()//to meters
    }

    fun getDistanceBetweenTwoPoint(hlat: Double, hlong: Double, glat: Double, glong: Double): Double {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        val lat1 = Math.toRadians(hlat)
        val lon1 = Math.toRadians(hlong)

        val lat2 = Math.toRadians(glat)
        val lon2 = Math.toRadians(glong)

        // Haversine formula
        val dlon = lon2 - lon1;
        val dlat = lat2 - lat1;
        val a = sin(dlat / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2.0)

        val c = 2 * asin(sqrt(a))

        // Radius of earth in kilometers. Use 3956
        // for miles
        val r = 6371

        // calculate the result distance is in kilometers
        return(c * r)/1000// converting to meters
    }

    fun getAngleBetweenTwoPoint(hlat: Double, hlong: Double, glat: Double, glong: Double): Double {
        // angle in radians
//        val angleRadians = atan2(glong - hlong, glat - hlat)
        // angle in degrees
        val angleDegree =  atan2(glong - hlong, glat - hlat) * 180 / Math.PI
        val angle = angleDegree
        return angle
    }

    fun locationListener(flpc: FusedLocationProviderClient, permissionid:Int, function:(task: Task<Location>)->Unit){
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions(permissionid)
            return
        }
        function(flpc.lastLocation)
    }

}