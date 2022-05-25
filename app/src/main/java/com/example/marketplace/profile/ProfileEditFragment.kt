package com.example.marketplace.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.example.marketplace.data.UserDatabase
import com.example.marketplace.data.VendorDataClass
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.tool.Tool
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.math.roundToInt


class ProfileEditFragment : Fragment() {

    private lateinit var screen_view: View
    private lateinit var id_et_signin_name: EditText
    private lateinit var id_et_signin_password: EditText
    private lateinit var id_et_signin_phonenumber: EditText
    private lateinit var id_et_signin_storename: EditText
    private lateinit var id_et_signin_address: EditText
    private lateinit var id_tv_save_location: TextView
    private lateinit var id_image_profile: ImageView
    private lateinit var id_iv_show_password: Button
    private lateinit var id_btn_cancel: Button
    private lateinit var id_btn_save: Button
    private lateinit var id_btn_save_location: Button


    private lateinit var image_file_uri: Uri
    private lateinit var firebase_storage: FirebaseStorage
    private lateinit var storage_refrence: StorageReference
    private lateinit var db_refrence: DatabaseReference
    private lateinit var firebase_auth: FirebaseAuth
    private lateinit var firebase_manager: FirebaseManager

    private lateinit var user_name:String
    private lateinit var user_type:String
    private lateinit var user_password:String

    private lateinit var user_database: UserDatabase

    private lateinit var fused_location_provider_client: FusedLocationProviderClient

    private  var returned_task = false
    private var is_profile_image_changed = false


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null){
            image_file_uri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image_file_uri)
            id_image_profile.setImageBitmap(bitmap)
            is_profile_image_changed = true
        }else if (requestCode == Constant.REQUEST_IMAGE_CAMERA && resultCode == Activity.RESULT_OK &&
            data != null){
                storage_refrence.child(image_file_uri.toString()).delete()
                val photo = data.extras?.get("data") as Bitmap
                image_file_uri = getImageUri(this.requireContext(), photo)
                val finalfile = File(getRealPathFromUri(image_file_uri))
                id_image_profile.setImageBitmap(photo)
            is_profile_image_changed = true
        }else Toast.makeText(requireContext(), "Could not get image", Toast.LENGTH_LONG).show()
    }

    private fun getRealPathFromUri(uri: Uri): String {
        var path = ""
        if (requireActivity().contentResolver != null){
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            if(cursor != null){
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    private fun getImageUri(cont: Context, bitm: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitm.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(cont.contentResolver, bitm,"title", null)
        return Uri.parse(path)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_IMAGE_CAMERA)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, Constant.REQUEST_IMAGE_CAMERA)
            }else{
                Toast.makeText(requireContext(), "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
        //CHECK IF REQUEST CODE IS GRANTED
        if (requestCode == Constant.REQUEST_LOCATION_PERMISSION){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Tool.showShortToast(requireContext(), "Access Granted")
                getCurrentLocation()
            }else{
                Tool.showShortToast(requireContext(), "Access Denied.. Please Grant")
            }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase_auth = FirebaseAuth.getInstance()
        firebase_storage = FirebaseStorage.getInstance()
        storage_refrence = firebase_storage.reference
        db_refrence = FirebaseDatabase.getInstance().reference

        user_name = Constant.getString(requireContext(), Constant.USERNAME).toString()
        user_type = Constant.getString(requireContext(), Constant.USER_TYPE).toString()
        user_password = Constant.getString(requireContext(), Constant.PASSWORD).toString()
        user_database = UserDatabase(requireContext(), Constant.USER_DB_NAME)
        image_file_uri = user_database.getFromDataBase()!!.imguri.toUri()
        firebase_manager = FirebaseManager(requireContext(), requireActivity())
        if(user_database.getFromDataBase()!!.imguri.isNotEmpty()){
            image_file_uri = user_database.getFromDataBase()!!.imguri.toUri()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        //initialise the views
        id_et_signin_name = screen_view.findViewById(R.id.id_et_signin_name)
        id_et_signin_password = screen_view.findViewById(R.id.id_et_signin_password)
        id_et_signin_phonenumber = screen_view.findViewById(R.id.id_et_signin_phonenumber)
        id_et_signin_storename = screen_view.findViewById(R.id.id_et_signin_store_name)
        id_et_signin_address = screen_view.findViewById(R.id.id_et_signin_address)
        id_btn_cancel = screen_view.findViewById(R.id.id_btn_cancel)
        id_btn_save = screen_view.findViewById(R.id.id_btn_save)
        id_image_profile = screen_view.findViewById(R.id.id_image_profile)
        id_btn_save_location = screen_view.findViewById(R.id.id_btn_save_location)
        id_iv_show_password = screen_view.findViewById(R.id.id_iv_show_password)
        id_tv_save_location = screen_view.findViewById(R.id.id_tv_save_location)

        configForCustomer()
        imageClickListener()
        saveButtonClickListener()
        cancelButtonClicked()
        saveLocation()
        fillUserDetail()

        id_iv_show_password.setOnClickListener {
            if (id_iv_show_password.text.equals("Show")) {
//                id_et_login_password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                id_et_signin_password.transformationMethod = null
                id_iv_show_password.text = "Hide"
            } else {
                id_et_signin_password.transformationMethod = PasswordTransformationMethod()
                id_iv_show_password.text = "Show"
            }
        }

        return screen_view.rootView
    }

    private fun configForCustomer() {
        if (user_type == "customer"){
            id_tv_save_location.visibility = TextView.INVISIBLE
            id_btn_save_location.visibility = Button.INVISIBLE
        }
    }


    private fun cancelButtonClicked() {
        id_btn_cancel.setOnClickListener { v ->
            Navigation.findNavController(screen_view).navigate(R.id.profileEditFragment_to_profileFragment)
            Toast.makeText(activity, "profile not updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveButtonClickListener(){
        id_btn_save.setOnClickListener {
            try {
                firebase_manager.deleteFromStorageDatabase(user_database.getFromDataBase()?.imguri.toString())
            }catch (e:IllegalArgumentException){
                Tool.debugMessage(e.toString(), tag="Error")
            }
            uploadImageAndUpdateVendor()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cr = context?.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr?.getType(uri))
    }

    private fun uploadImageAndUpdateVendor(){
        val username = id_et_signin_name.text.toString()
        val password = id_et_signin_password.text.toString()
        if (username.contains("_")) {
            Tool.showShortToast(requireContext(), "$username cannot contain underscore")
            return
        }
        if(username.lowercase() != user_name.lowercase() || password != user_password){
            updateAllUserData(username, password)
            return
        }
        if(is_profile_image_changed){
            updateDatabase()
            return
        }
        updateOtherDatab()
    }

    private fun updateOtherDatab() {
        //loadind progress bar
        Tool.loadingProgressBar(requireContext(), "Updating... please wait")
        { probar ->
            val path =
                user_type + "/" + user_type + "_" + user_name + "_" + user_password + "/user"
            val data = user_database.getFromDataBase()
            if (data != null) {
                //check if uri is empty
                var imguri = ""
                if (data.imguri.isNotEmpty()) imguri = data.imguri
                if (image_file_uri.toString().isNotEmpty()) imguri =
                    image_file_uri.toString()
                // add the uri you downloaded to online firebase database
                firebase_manager.addToFirebaseDatabase(path, VendorDataClass(
                    imguri = imguri,
                    username = id_et_signin_name.text.toString(),
                    password = id_et_signin_password.text.toString(),
                    phonenumber = id_et_signin_phonenumber.text.toString(),
                    storename = id_et_signin_storename.text.toString(),
                    address = id_et_signin_address.text.toString(),
                    latitude = data.latitude,
                    longitude = data.longitude
                )).addOnCompleteListener {
                    probar.dismiss()
                    findNavController().navigate(R.id.profileEditFragment_to_profileFragment)
                }
            }
        }
    }

    // THIS FUNCTION IS FOR UPDATING USER, NOT CHANGING USER NAME OR PASSWORD
    private fun updateDatabase() {
        //loadind progress bar
        Tool.loadingProgressBar(requireContext(), "Updating... please wait")
        { probar ->
            // make a reference to your online database and store the image
            val refrencepath =
                storage_refrence.child("vendorimage/" + UUID.randomUUID().toString())
            refrencepath.putFile(image_file_uri)
                .addOnSuccessListener {
                    refrencepath.downloadUrl.addOnSuccessListener {
                        //dowmload the uri of the image
                        image_file_uri = it
                        //put the path in storage
                        Constant.setString(requireContext(),
                            Constant.IMAGE_URI,
                            it.toString())
                        val path =
                            user_type + "/" + user_type + "_" + user_name + "_" + user_password + "/user"
                        val data = user_database.getFromDataBase()
                        if (data != null) {
                            //check if uri is empty
                            var imguri = ""
                            if (data.imguri.isNotEmpty()) imguri = data.imguri
                            if (image_file_uri.toString().isNotEmpty()) imguri =
                                image_file_uri.toString()
                            // add the uri you downloaded to online firebase database
                            firebase_manager.addToFirebaseDatabase(path, VendorDataClass(
                                imguri = imguri,
                                username = id_et_signin_name.text.toString(),
                                password = id_et_signin_password.text.toString(),
                                phonenumber = id_et_signin_phonenumber.text.toString(),
                                storename = id_et_signin_storename.text.toString(),
                                address = id_et_signin_address.text.toString(),
                                latitude = data.latitude,
                                longitude = data.longitude
                            ))
                        }
                        probar.dismiss()
                        Navigation.findNavController(screen_view)
                            .navigate(R.id.profileEditFragment_to_profileFragment)
                    }
                }
                .addOnFailureListener {
                    Tool.debugMessage(it.toString(), "EXECPTION")
                    Toast.makeText(this.context,
                        "Failed! could not upload image",
                        Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                }
        }
    }

    // THIS FUNCTION IS FOR UPDATING USER, THAT IS CHANGING USER NAME OR PASSWORD
    private fun updateAllUserData(username: String, password: String){
        Tool.popUpDisplay(requireContext())
        {popup ->
            popup.setTitle("Update in Progress")
            popup.setMessage(" Do you want to update your profile")
            popup.setPositiveButton("Update"){_,_ ->
                Tool.loadingProgressBar(requireContext(), "Updating Profile")
                { probar ->
                    val refrencepath =
                        storage_refrence.child("vendorimage/" + UUID.randomUUID().toString())
                    refrencepath.putFile(image_file_uri)
                        .addOnSuccessListener {
                            refrencepath.downloadUrl.addOnSuccessListener {
                                image_file_uri = it
                                Tool.debugMessage(image_file_uri.toString(), "URI")
                                //put the path in storage
                                Constant.setString(requireContext(),
                                    Constant.IMAGE_URI,
                                    it.toString())
                                updateUser(username, password)
                                probar.dismiss()
                            }
                        }
                        .addOnFailureListener {
                            Tool.debugMessage(it.toString(), "EXECPTION")
                            Toast.makeText(this.context,
                                "Failed! could not upload image",
                                Toast.LENGTH_SHORT).show()
                        }
                        .addOnProgressListener {
                        }
                }
            }
            popup.setNegativeButton("back", null)
        }
    }

    private fun updateUser(username: String, password: String) {
        val phonenumber = id_et_signin_phonenumber.text.toString().trim()
        val storename = id_et_signin_storename.text.toString().trim()
        val address = id_et_signin_address.text.toString().trim()
        val oldpath = user_type+"/"+user_type+"_"+user_name+"_"+user_password
        val path = user_type+"/"+user_type+"_"+username+"_"+password
        db_refrence.child(oldpath).get()
            .addOnSuccessListener { datasnap ->
                db_refrence.child(path).setValue(datasnap.value)
                db_refrence.child(oldpath).removeValue()
                //UPDATE THE USER
                firebase_manager.addToFirebaseDatabase("$path/user", VendorDataClass(
                    imguri = image_file_uri.toString(),
                    username = username,
                    password = password,
                    phonenumber = phonenumber,
                    storename = storename,
                    address = address
                ))
                deleteCurrentUser()
                val email = Constant.getVendorEmailHack(username)
                firebase_auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            requireActivity().finish()
                            startActivity(Intent(requireContext(),
                                MainActivity::class.java))
                            Tool.showShortToast(requireContext(),
                                "Update Successful")
                        } else {
                            Tool.showShortToast(requireContext(),
                                "Cannot update Vendor")
                        }
                    }
            }
    }

    private fun deleteCurrentUser(): Boolean {
        val user = firebase_auth.currentUser
        val email = user_type+"_$user_name"
        val password = user_password
        val credential = EmailAuthProvider.getCredential(email, password)
        user?.reauthenticate(credential)?.addOnCompleteListener {
            user.delete()
                .addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        returned_task = true
                    }
                }
        }
        val bool: Boolean = returned_task
        returned_task = false
        return bool
    }

    private fun imageClickListener() {
        id_image_profile.setOnClickListener{
            val popup = AlertDialog.Builder(requireContext())
            popup.setTitle("Get Image From")
            popup.setPositiveButton("gallery") { _: DialogInterface, _: Int ->
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
//                startActivityForResult(intent, Constant.REQUEST_IMAGE_GALLERY)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constant.REQUEST_IMAGE_GALLERY)
            }
            popup.setNegativeButton("camera") { _: DialogInterface, _: Int ->
//                        takePicture()
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    activity?.let {
                        takePictureIntent.resolveActivity(it.packageManager)?.also {
                            val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                            if (permission != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                                    Manifest.permission.CAMERA), 1)
                            }else{
                                startActivityForResult(takePictureIntent, Constant.REQUEST_IMAGE_CAMERA)
                            }
                        }
                    }
                }
            }
            popup.show()
        }
    }

    private fun createVendor(username:String, password:String) {
        //initialize the rest of the view
        val phonenumber = id_et_signin_phonenumber.text.toString()
        val storename = id_et_signin_storename.text.toString()
        val address = id_et_signin_address.text.toString()

        Tool.loadingProgressBar(requireContext(), "Updating profile... Please wait"){probar ->
            firebase_auth.createUserWithEmailAndPassword(Constant.getVendorEmailHack(username), password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        probar.dismiss()
                        addVendor(username, password, phonenumber, storename, address)
                        Navigation.findNavController(screen_view)
                            .navigate(R.id.profileEditFragment_to_profileFragment)
                    }else{
                        probar.dismiss()
                        Constant.showShortToast(requireContext(), "Cannot create Vendor")
                    }
                }
        }
    }

    private fun addVendor(username: String, password: String, phonenumber: String, storename: String, address: String ): VendorDataClass {
        db_refrence.push()
        db_refrence.child(Constant.VENDOR)
            .child("vendor_"+username+"_"+password)
            .child("user")
            .setValue(VendorDataClass(
                imguri = image_file_uri.toString(),
                username = username,
                password = password,
                phonenumber = phonenumber,
                storename = storename,
                address = address)
            )
        return VendorDataClass(image_file_uri.toString(), username, password, phonenumber, storename, address)
    }

    private fun updateUserAuthentication(username: String, password: String){
        //initialize the rest of the view
        val phonenumber = id_et_signin_phonenumber.text.toString()
        val storename = id_et_signin_storename.text.toString()
        val address = id_et_signin_address.text.toString()
        //pop up to ask for update confirmation
        Tool.popUpDisplay(requireContext())
        {popup ->
            popup.setTitle("Do you want to update profile")
            popup.setPositiveButton("Update") { _, _ ->
                //loading bar to animate the updating sequence
                Tool.loadingProgressBar(requireContext(), "Updating..."){probar ->
                val user = firebase_auth.currentUser
                val data = user_database.getFromDataBase()
                    // actually updating the authentication
                user?.updateEmail(Constant.getVendorEmailHack(data?.username!!))
                user?.updatePassword(data?.password!!)
                    ?.addOnCompleteListener(requireActivity()) { task ->
                        //check if it was complete
                        if (task.isSuccessful) {
                            val vdc = addVendor(username, password, phonenumber, storename, address)
                            probar.dismiss()
                            Tool.showShortToast(requireContext(), "Profile updated")
                            user_database.deleteDatabaseTable()
                            user_database.insertIntoDatabase(vdc)
                            Navigation.findNavController(screen_view)
                                .navigate(R.id.profileEditFragment_to_profileFragment)
                        }else{
                            probar.dismiss()
                            Constant.showShortToast(requireContext(), "Cannot create Vendor")
                        }
                    }
                }
            }
            popup.setNegativeButton("Back"){_,_ ->
                Tool.showShortToast(requireContext(), "Profile not updated")
            }
        }
    }

    // LOCATION PART OF THIS PROGRAMME

    private fun saveLocation() {
        id_btn_save_location.setOnClickListener {
            fused_location_provider_client = LocationServices.getFusedLocationProviderClient(requireContext())
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation(){
        //check if permission is granted
        if (checkPermission()){
            //check if permission is enabled
            if (isLocationEnabled()){
                //FIND LONG AND LAT
                fused_location_provider_client.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location = task.result
                    if (location == null) {
                        Tool.showShortToast(requireContext(), "Can not find location")
                    } else {
                        db_refrence.child(Constant.VENDOR)
                            .child("vendor_"+user_name+"_"+user_password)
                            .child("user")
                            .child("latitude")
                            .setValue(location.latitude)
                        db_refrence.child(Constant.VENDOR)
                            .child("vendor_"+user_name+"_"+user_password)
                            .child("user")
                            .child("longitude")
                            .setValue(location.longitude)
                        Tool.showShortToast(requireContext(), "location find. Successful")
                        val distance = "Lat: ${location.latitude.roundToInt()} Long: ${location.longitude.roundToInt()}"
                        id_tv_save_location.text = distance
                    }
                }
            }else{
                //setting to open location
                Tool.showShortToast(requireContext(), "Location not enabled, Turn on location")
                // OPEN LOCATION SETTINGS
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            // ask for permission
            requestPermissions()
        }
    }
    
    private fun requestPermissions(){
        //REQUEST FOR LOCATION PERMISSION
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            Constant.REQUEST_LOCATION_PERMISSION
        )
    }

    private fun checkPermission():Boolean {
        //CHECK FOR PERMISSION
        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun isLocationEnabled():Boolean {
        val locationmanager: LocationManager = requireActivity()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun fillUserDetail(){
        val vdc = user_database.getFromDataBase()
        try {
            Picasso.get().load(vdc?.imguri).into(id_image_profile)
        }catch(e:IllegalArgumentException){}
        id_et_signin_name.setText(vdc?.username)
        id_et_signin_password.setText(vdc?.password)
        id_et_signin_phonenumber.setText(vdc?.phonenumber)
        id_et_signin_storename.setText(vdc?.storename)
        id_et_signin_address.setText(vdc?.address)
        var lat = 0
        var long = 0
        if (vdc?.latitude?.isNotEmpty() == true){
            lat = vdc.latitude.toFloat().roundToInt()
        }
        if (vdc?.latitude?.isNotEmpty() == true){
            long = vdc.longitude.toFloat().roundToInt()
        }
        val mess = "Lat: $lat Long: $long"
        id_tv_save_location.text = mess
    }

}
