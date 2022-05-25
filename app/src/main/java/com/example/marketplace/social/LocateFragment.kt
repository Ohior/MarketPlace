package com.example.marketplace.social

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.marketplace.R
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.Locator
import com.example.marketplace.tool.Tool
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis
import android.view.animation.Animation

import android.view.animation.RotateAnimation
import com.example.marketplace.social.Compass.CompassListener
import android.hardware.SensorManager
import com.example.marketplace.data.UserDatabase
import com.example.marketplace.shop.ShopActivity
import java.math.BigDecimal
import java.math.MathContext


class LocateFragment : Fragment(), SensorEventListener {

    private lateinit var screen_view: View
    private lateinit var id_store_name: TextView
    private lateinit var id_tv_distance: TextView
    private lateinit var id_iv_locator: ImageView
    private lateinit var id_tv_info: TextView

    private var location_angle: Double = 0.0
    private var location_distance: Double = 0.0

    private var user_latitude = 0.0
    private var user_longitude = 0.0
    private var vendor_latitude = 0.0
    private var vendor_longitude = 0.0

    private lateinit var compass_sensor_manager: SensorManager
    private var accelerometer_sensor: Sensor? = null
    private var magnetometer_sensor: Sensor? = null
    private var accel_read = FloatArray(3)
    private var magnetic_read = FloatArray(3)
    private val current_degree = 0f
    private var azimuth_angle = 0f

    private lateinit var locator_manager: Locator

    private lateinit var vendor_database: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vendor_database = UserDatabase(requireContext(), Constant.VENDOR_DB_NAME)
        compass_sensor_manager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        get accelerometer hardware from device
        //        get accelerometer hardware from device
        accelerometer_sensor = compass_sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        get magnetometer hardware from device
        //        get magnetometer hardware from device
        magnetometer_sensor = compass_sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        locator_manager = Locator(requireActivity(), requireContext())
        // get the vendor (not the user) lat and long
        try {
            vendor_latitude = vendor_database.getFromDataBase()!!.latitude.toDouble()
            vendor_longitude = vendor_database.getFromDataBase()!!.latitude.toDouble()
        }catch (e:NumberFormatException){
            startActivity(Intent(requireContext(), ShopActivity::class.java))
            Tool.showShortToast(requireContext(), "Vendor does not have a location")
        }
    }

    override fun onPause() {
        super.onPause()
//        the sensors are unregistered (disconnected) in the onPause()
//method when the activity pauses
        compass_sensor_manager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        //        the sensor listeners are registered meaning
//        that the sensors are powered on again when the activity resumes
        compass_sensor_manager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_UI)
        compass_sensor_manager.registerListener(this, magnetometer_sensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        screen_view = inflater.inflate(R.layout.fragment_locate, container, false)
        id_store_name = screen_view.findViewById(R.id.id_store_name)
        id_iv_locator = screen_view.findViewById(R.id.id_iv_locator)
        id_tv_info = screen_view.findViewById(R.id.id_tv_info)
        id_tv_distance = screen_view.findViewById(R.id.id_tv_distance)

        return screen_view
    }

    override fun onSensorChanged(event: SensorEvent) {
        val tvdegrees = id_tv_info
        val ivcompass = id_iv_locator

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accel_read = event.values
        }

        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetic_read = event.values
        }

        val r = FloatArray(9)
        val i = FloatArray(9)

        //  to get the rotation matrix R of the device as follows
        val successfulread = SensorManager.getRotationMatrix(r, i, accel_read, magnetic_read)
        //  If this operation is successful, the successful_read variable will be
        //  true and the rotation matrix will be stored in the variable R

        if (successfulread) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(i, orientation)
            azimuth_angle = orientation[0]
            // adjust angle to user location
            azimuth_angle -= Tool.getBearing(user_latitude, user_longitude, vendor_latitude, vendor_longitude).toFloat()
            val degrees = azimuth_angle * 180f / 3.14f
            val degreesInt = degrees.toString()

            val mess = "${String.format("%.2f", degreesInt)} ${0x00B0.toChar()} from North ${
                locator_manager.getDistanceBetweenTwoPoint(
                    user_latitude, user_longitude, vendor_latitude, vendor_longitude
                ).roundToInt()
            } Meter(s)"

            tvdegrees.text = mess
            //  declare a RotateAnimation object to Rotate the image on imageView
            //  val rotate = RotateAnimation(current_degree,
            //      (-degreesInt).toFloat(), Animation.RELATIVE_TO_SELF,
            //      0.5f, Animation.RELATIVE_TO_SELF, 0.0f)
//            val rotate = RotateAnimation(current_degree, degrees, Animation.RELATIVE_TO_SELF,
//                0.5f, Animation.RELATIVE_TO_SELF, 0.0f)
//            //            set the animation Duration
//            rotate.duration = 100
//            rotate.fillAfter = true
//            //            rotate the imageview
//            ivcompass.startAnimation(rotate)
            Tool.rotateAnimation(ivcompass, degrees, 100)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}
//class LocateFragment : Fragment(){
//    private lateinit var screen_view: View
//    private lateinit var id_store_name: TextView
//    private lateinit var id_tv_distance: TextView
//    private lateinit var id_iv_locator: ImageView
//    private lateinit var id_tv_info: TextView
//
//    private var location_angle: Double = 0.0
//    private var location_distance: Double = 0.0
//
//    private var user_latitude = 0.0
//    private var user_longitude = 0.0
//    private var vendor_latitude = 0.0
//    private var vendor_longitude = 0.0
//


//    private lateinit var compass_class: Compass
//    private var currentAzimuth = 0.0F
//    private lateinit var sotw_formatter: SOTWFormatter
//
//        private var mHandler: Handler? = null
//    private val mInterval = 1000 // 5 seconds by default, can be changed later
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // get the vendor (not the user) lat and long
//        vendor_latitude = Constant.getString(requireContext(), Constant.LATITUDE)?.toDouble()!!
//        vendor_longitude = Constant.getString(requireContext(), Constant.LONGITUDE)?.toDouble()!!
//        sotw_formatter = SOTWFormatter(requireContext())
//        mHandler = Handler()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        compass_class.start()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        compass_class.stop()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        compass_class.start()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        compass_class.stop()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        screen_view = inflater.inflate(R.layout.fragment_locate, container, false)
//        id_store_name = screen_view.findViewById(R.id.id_store_name)
//        id_iv_locator = screen_view.findViewById(R.id.id_iv_locator)
//        id_tv_info = screen_view.findViewById(R.id.id_tv_info)
//        id_tv_distance = screen_view.findViewById(R.id.id_tv_distance)
//
//
//        setUpCompass()
//        return screen_view
//    }
//
//    private fun setUpCompass() {
//        compass_class = Compass(requireContext())
//        val cl = getCompassListener()
//        compass_class.setListener(cl)
//    }
//
//    private fun adjustArrow(azimuth:Float){
//        val an: Animation = RotateAnimation(-currentAzimuth, -azimuth,
//            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//            0.5f)
//        currentAzimuth = azimuth
//
//        an.duration = 500
//        an.repeatCount = 0
//        an.fillAfter = true
//
//        id_iv_locator.startAnimation(an)
//    }
//
//    private fun  adjustSotwLabel(azimuth: Float){
//        id_tv_info.text = sotw_formatter.format(azimuth)
//    }
//
//    private fun getCompassListener(): Compass.CompassListener? {
//        return object : CompassListener {
//            override fun onNewAzimuth(azimuth: Float) {
//                // UI updates only in UI thread
//                // https://stackoverflow.com/q/11140285/444966
//                locateThread {
//                    adjustArrow(azimuth)
//                    adjustSotwLabel(azimuth)
////                    currentAzimuth -= Tool.getBearing(user_latitude, user_longitude, vendor_latitude, vendor_longitude).toFloat()
//                    Tool.debugMessage(currentAzimuth.toString(), "BEARING")
//                }.run()
//            }
//        }
//    }
//
//    private fun locateThread(function:()->Unit):Runnable{
//        val mStatusChecker: Runnable = object : Runnable {
//            override fun run() {
//                try {
//                    function()
//                } finally {
//                    // 100% guarantee that this always happens, even if
//                    // your update method throws an exception
//                    mHandler!!.postDelayed(this, mInterval.toLong())
//                }
//            }
//        }
//        return mStatusChecker
//    }
//}

//class LocateFragment : Fragment() {
//    private lateinit var screen_view: View
//    private lateinit var id_store_name: TextView
//    private lateinit var id_tv_distance: TextView
//    private lateinit var id_iv_locator: ImageView
//    private lateinit var id_tv_info: TextView
//
//    private var location_angle: Double = 0.0
//    private var location_distance: Double = 0.0
//
//    private var user_latitude = 0.0
//    private var user_longitude = 0.0
//    private var vendor_latitude = 0.0
//    private var vendor_longitude = 0.0
//
//    private lateinit var locator_manager: Locator
//
//    private lateinit var location_request: LocationRequest
//    private lateinit var location_callback: LocationCallback
//
//
//    private val mInterval = 1000 // 5 seconds by default, can be changed later
//
//    private var mHandler: Handler? = null
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray,
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == Constant.REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty()){
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                setLocationUpdate()
//            }else{
//                stopLocationService()
//                Tool.showShortToast(requireContext(), "Permission is required for location to work properly")
//            }
//        }
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requireActivity().actionBar?.hide()
//
//        locator_manager = Locator(requireActivity(), requireContext())
//        // check if you have permission to use permission the request
//        if (!locator_manager.checkLocationPermission())
//            locator_manager.requestLocationPermissions(Constant.REQUEST_LOCATION_PERMISSION)
//        // check if location is enabled
//        if (!locator_manager.isLocationEnabled()) locator_manager.openLocationSetting()
//
//        // get the vendor (not the user) lat and long
//        vendor_latitude = Constant.getString(requireContext(), Constant.LATITUDE)?.toDouble()!!
//        vendor_longitude = Constant.getString(requireContext(), Constant.LONGITUDE)?.toDouble()!!
//        setHasOptionsMenu(false)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        screen_view = inflater.inflate(R.layout.fragment_locate, container, false)
//        id_store_name = screen_view.findViewById(R.id.id_store_name)
//        id_iv_locator = screen_view.findViewById(R.id.id_iv_locator)
//        id_tv_info = screen_view.findViewById(R.id.id_tv_info)
//        id_tv_distance = screen_view.findViewById(R.id.id_tv_distance)
//
//        mHandler = Handler()
//        mStatusChecker.run()
//
//
//        locationCallback()
//
//        locationRequest()
//
//        setLocationUpdate()
//
//        return screen_view
//    }
//
//    var mStatusChecker: Runnable = object : Runnable {
//        override fun run() {
//            try {
//                rotateLocatorImage()
//            } finally {
//                // 100% guarantee that this always happens, even if
//                // your update method throws an exception
//                mHandler!!.postDelayed(this, mInterval.toLong())
//            }
//        }
//    }
//
//
//    private fun rotateLocatorImage() {
//        val angle = locator_manager.getAngleBetweenTwoPoint(
//            user_latitude,
//            user_longitude,
//            vendor_latitude,
//            vendor_longitude
//        )
//        val distance1 = locator_manager.getDistanceBetweenTwoPosition(
//            user_latitude,
//            user_longitude,
//            vendor_latitude,
//            vendor_longitude
//        )
//        val distance2 = locator_manager.getDistanceBetweenTwoPoint(
//            user_latitude,
//            user_longitude,
//            vendor_latitude,
//            vendor_longitude
//        )
//
//        id_iv_locator.setOnClickListener{
//            Tool.showShortToast(requireContext(), angle.toString())
//        }
//        Tool.rotateAnimation(id_iv_locator, angle.toFloat())
//        val message1 =  "You are ${distance1.roundToInt()} meters from you location (position)"
//        val message2 =  "You are ${distance2.roundToInt()} meters from you location (point) $angle"
//        id_tv_distance.text = message1
//        id_tv_info.text = message2
//    }
//
//    private fun locationRequest(){
//        location_request = LocationRequest()
//        location_request.interval = 4000
//        location_request.fastestInterval = 2000
//        location_request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//    }
//
//    private fun locationCallback(){
//        location_callback = object: LocationCallback(){
//            override fun onLocationResult(lr: LocationResult) {
//                super.onLocationResult(lr)
//                user_latitude = lr.lastLocation.latitude
//                user_longitude = lr.lastLocation.longitude
//                Tool.showShortToast(requireContext(), "Latitude $user_latitude Longitude $user_longitude")
//                Tool.debugMessage("Latitude $user_latitude Longitude $user_longitude")
//            }
//        }
//    }
//
//    private fun setLocationUpdate(){
//        if(locator_manager.checkLocationPermission()){
//            LocationServices.getFusedLocationProviderClient(requireContext())
//                .requestLocationUpdates(location_request, location_callback, Looper.getMainLooper())
//            startLocationService()
//        }else{
//            stopLocationService()
//            locator_manager.requestLocationPermissions(Constant.REQUEST_LOCATION_PERMISSION)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        LocationServices.getFusedLocationProviderClient(requireContext())
//            .removeLocationUpdates(location_callback)
//        stopLocationService()
//    }
//
//    private fun startLocationService(){
//        val intent = Intent(requireContext(), NotificationService::class.java)
//        intent.action = Constant.ACTION_START_LOCATION_SERVICE
//        requireContext().startService(intent)
//        Tool.showShortToast(requireContext(), "Location Service Started")
//
//    }
//
//    private fun stopLocationService(){
//        val intent = Intent(requireContext(), NotificationService::class.java)
//        intent.action = Constant.ACTION_STOP_LOCATION_SERVICE
//        requireContext().startService(intent)
//        Tool.showShortToast(requireContext(), "Location Service Stop")
//
//    }
//}