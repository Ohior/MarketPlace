package com.example.marketplace.social

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.marketplace.R
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.Tool
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit


class LocateFragment : Fragment() {
    private val PERMISSION_ID: Int = 1000
    private lateinit var screen_view: View
    private lateinit var id_store_name: TextView
    private lateinit var id_tv_distance: TextView
    private lateinit var id_iv_locator: ImageView
    private lateinit var id_btn_left: Button
    private lateinit var id_btn_right: Button
    private var float_angle: Float = 0.0f
    private var float_distance: Float = 0.0f
    private var super_latitude: Double = 0.0
    private var super_longitude: Double = 0.0

//    private lateinit var fused_location_provider_client: FusedLocationProviderClient
//    private lateinit var location_request: LocationRequest
    private lateinit var location_manager: LocationManager
    private var current_location: Location? = null
    private lateinit var location_by_Gps: Location
    private lateinit var location_by_network: Location

    // Creating an instance of GPS LocationListener (package: android.location)
    private val gps_location_listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            location_by_Gps= location
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    // Creating an instance of GPS LocationListener (package: android.location)
    val network_location_listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            location_by_network = location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fused_location_provider_client: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e.,
// how often you should receive updates, the priority, etc.
    private lateinit var location_request: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient
// has a new Location
    private lateinit var location_callback: LocationCallback


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.shop_menu, menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        return super.onOptionsItemSelected(item)
//        return when (item.itemId){
//            R.id.id_menu_location ->{
//                Navigation.findNavController(screen_view).navigate(R.id.marketFragment_to_locateFragment)
//                Toast.makeText(requireContext(), "select all", Toast.LENGTH_SHORT).show()
//                true
//            }
//            else -> {
//                //this makes it so menu item works in fragment
//                NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
//                        || super.onOptionsItemSelected(item)
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //instantiate location variables
        location_manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fused_location_provider_client = LocationServices.getFusedLocationProviderClient(requireActivity())
        // Initialize locationRequest.
        location_request = LocationRequest().apply {
            // Sets the desired interval for
            // active location updates.
            // This interval is inexact.
            interval = TimeUnit.SECONDS.toMillis(60)

            // Sets the fastest rate for active location updates.
            // This interval is exact, and your application will never
            // receive updates more frequently than this value
            fastestInterval = TimeUnit.SECONDS.toMillis(30)

            // Sets the maximum time when batched location
            // updates are delivered. Updates may be
            // delivered sooner than this interval
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //Initialize locationCallback.

        location_callback = object : LocationCallback() {
            override fun onLocationResult(locationresult: LocationResult) {
                super.onLocationResult(locationresult)
                locationresult.lastLocation.let {
                    current_location = location_by_Gps
                    super_latitude = current_location!!.latitude
                    super_longitude = current_location!!.longitude
                    // use latitude and longitude as per your need
                }
            }
        }

        //let the FusedLocationProviderClient know that you want to receive updates
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Looper.myLooper()?.let {
                fused_location_provider_client.requestLocationUpdates(location_request, location_callback, it)
            }
            return
        }

        //When the app no longer needs access to location information, it’s important to unsubscribe from location updates.
        val removeTask = fused_location_provider_client.removeLocationUpdates(location_callback)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Constant.debugMessage("Location Callback removed.")
                Tool.showShortToast(requireContext(), "Location Callback removed.")
            } else {
                Constant.debugMessage("Failed to remove Location Callback.")
                Tool.showShortToast(requireContext(), "Failed to remove Location Callback.")
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view = inflater.inflate(R.layout.fragment_locate, container, false)
        id_store_name = screen_view.findViewById(R.id.id_store_name)
        id_iv_locator = screen_view.findViewById(R.id.id_iv_locator)
        id_btn_left = screen_view.findViewById(R.id.id_btn_left)
        id_btn_right = screen_view.findViewById(R.id.id_btn_right)
        id_tv_distance = screen_view.findViewById(R.id.id_tv_distance)

//        leftButtonClicked()
//        rightButtonClicked()
//        LocatorFunction()

        isLocationPermissionGranted()
        locationExecution()
        mostAccurateLocation()
        workWithLocation { latitude, longitude ->
            Tool.debugMessage("$latitude and $longitude")
        }

        //configure this fragment for menu
        setHasOptionsMenu(true)
        return screen_view.rootView
    }

    private fun workWithLocation(function:(Double, Double)->Unit){
        if (location_by_Gps.accuracy > location_by_network.accuracy) {
            current_location = location_by_Gps
            super_latitude = current_location!!.latitude
            super_longitude = current_location!!.longitude
            // use latitude and longitude as per your need
        } else {
            current_location = location_by_network
            super_latitude = current_location!!.latitude
            super_longitude = current_location!!.longitude
            // use latitude and longitude as per your need
        }
        function(super_latitude, super_longitude)
    }

    private fun locationExecution() {
        if (isGPSEnabled()){
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                location_manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0f,
                    gps_location_listener
                )
            }
        }
        if (isNetworkEnabled()){
            location_manager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                network_location_listener
            )
        }
    }

    // check for most accurate location
    private fun mostAccurateLocation(){
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            val last_kown_location_by_GPS = location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            last_kown_location_by_GPS?.let {
                location_by_Gps = last_kown_location_by_GPS
            }
        }

        val last_kown_location_by_network =
            location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        last_kown_location_by_network?.let {
            location_by_network = last_kown_location_by_network
        }
    }

    private fun isGPSEnabled():Boolean{
    //  Check that if the GPS is available
        return location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }

    private fun isNetworkEnabled():Boolean{
    //  Check that if the GPS is available
        return location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isLocationPermissionGranted():Boolean{
        //  checking that location permission is granted or not.
        //  If Manifest.permission is not granted then ask for the permissions in run time
        return if(ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)
            false
        }else{
            true
        }
    }





















//    private fun LocatorFunction() {
//        Tool.rotateAnimation(id_iv_locator, float_angle)
//    }
//
//    // check for permissions
//    private fun checkForPermission():Boolean {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            return true
//        }
//        return false
//    }
//
//    // get user permission
//    private fun requestPermissions(){
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION),
//            PERMISSION_ID
//        )
//    }
//
//    // check if user location is enabled
//    private fun isLocationEnabled():Boolean {
//        val locationmanager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        return (locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//    }

    // get location
//    private fun getLocation(){
//        //check for permision
//        if (checkForPermission()) {
//            // check if location service is enabled
//            if (isLocationEnabled()){
//                //get the location
//                fused_location_provider_client.lastLocation.addOnCompleteListener { task ->
//                    val location = task.result
//                    if (location == null){
//                        // we will get new user location
//                    }else{
//                        val mess = "Your current  location are \n LAT: ${location.latitude}" +
//                                "and LONG: ${location.longitude}"
//                        id_tv_distance.text = mess
//                    }
//                }
//            }else{
//                Tool.showLongToast(requireContext(),  "Please Enable Your Location")
//            }
//        }else{
//            requestPermissions()
//        }
//    }
//
//    private fun rightButtonClicked() {
//        id_btn_right.setOnClickListener{
//            Tool.showShortToast(requireContext(), "Right button clicked")
//        }
//    }
//
//    private fun leftButtonClicked() {
//        id_btn_left.setOnClickListener{
//            Tool.showShortToast(requireContext(), "Left button clicked")
//        }
//    }
}