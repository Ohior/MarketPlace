package com.example.marketplace.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.example.marketplace.data.UserDatabase
import com.example.marketplace.data.VendorDataClass
import com.example.marketplace.market.MarketActivity
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.tool.Tool
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class CustomerProfileEditFragment : Fragment() {
    private lateinit var screen_view :View

    //make the views
    private lateinit var id_image_profile: ImageView
    private lateinit var id_et_signin_password: EditText
    private lateinit var id_et_signin_phonenumber: EditText
    private lateinit var id_et_signin_name: EditText
    private lateinit var id_btn_cancel: Button
    private lateinit var id_btn_sign_in: Button
    private lateinit var id_btn_customer: Button

    private lateinit var image_file_uri: Uri
    private  var returned_task = false

    private lateinit var user_name:String
    private lateinit var user_type:String
    private lateinit var user_password:String

    private lateinit var firebase_storage: FirebaseStorage
    private lateinit var storage_refrence: StorageReference
    private lateinit var db_refrence: DatabaseReference
    private lateinit var firebase_auth: FirebaseAuth
    private lateinit var firebase_manager: FirebaseManager

    private lateinit var user_database: UserDatabase


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
        return when (item.itemId){
            R.id.id_menu_chat -> {
                when (user_type) {
                    "null" -> {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        Toast.makeText(requireContext(), "$user_name Chat null", Toast.LENGTH_SHORT).show()
                        requireActivity().finish()
                    }
                    "customer" -> {
                        Navigation.findNavController(screen_view).navigate(R.id.customerProfileEditFragment_to_chatListFragment2)
                    }
                }
                true
            }
            R.id.id_menu_market ->{
                requireActivity().finish()
                startActivity(Intent(requireContext(), MarketActivity::class.java))
                true
            }
            R.id.id_menu_location ->{
                true
            }
            else -> {
                //this makes it so menu item works in fragment
                NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                        || super.onOptionsItemSelected(item)
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null){
            if (image_file_uri.toString().isNotEmpty()){
                storage_refrence.child(image_file_uri.toString()).delete()
            }
            image_file_uri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image_file_uri)
            id_image_profile.setImageBitmap(bitmap)
        }else if (requestCode == Constant.REQUEST_IMAGE_CAMERA && resultCode == Activity.RESULT_OK &&
            data != null){
            storage_refrence.child(image_file_uri.toString()).delete()
            val photo = data.extras?.get("data") as Bitmap
            image_file_uri = getImageUri(this.requireContext(), photo)
            val finalfile = File(getRealPathFromUri(image_file_uri))
            id_image_profile.setImageBitmap(photo)
        }else Toast.makeText(requireContext(), "Could not get image", Toast.LENGTH_LONG).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase_auth = FirebaseAuth.getInstance()
        firebase_storage = FirebaseStorage.getInstance()
        storage_refrence = firebase_storage.reference
        db_refrence = FirebaseDatabase.getInstance().reference

        user_database = UserDatabase(requireContext(), Constant.USER_DB_NAME)
        image_file_uri = user_database.getFromDataBase()!!.imguri.toUri()

        user_name = Constant.getString(requireContext(), Constant.USERNAME).toString()
        user_type = Constant.getString(requireContext(), Constant.USER_TYPE).toString()
        user_password = Constant.getString(requireContext(), Constant.PASSWORD).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view = inflater.inflate(R.layout.fragment_customer_profile_edit, container, false)
        id_image_profile = screen_view.findViewById(R.id.id_image_profile)
        id_et_signin_password = screen_view.findViewById(R.id.id_et_signin_password)
        id_et_signin_phonenumber = screen_view.findViewById(R.id.id_et_signin_phonenumber)
        id_et_signin_name = screen_view.findViewById(R.id.id_et_signin_name)
        id_btn_cancel = screen_view.findViewById(R.id.id_btn_cancel)
        id_btn_sign_in = screen_view.findViewById(R.id.id_btn_sign_in)
        id_btn_customer = screen_view.findViewById(R.id.id_btn_customer)


        imageClickListener()
        cancelButtonClicked()
        saveButtonClicked()
        setHasOptionsMenu(true)
        return screen_view.rootView
    }

    private fun saveButtonClicked() {
        id_btn_sign_in.setOnClickListener {
            uploadImageAndUpdateCustomer()
        }
    }

    private fun uploadImageAndUpdateCustomer() {
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
        updateDatabase()    }

    private fun updateDatabase() {
        //loadind progress bar
        Tool.loadingProgressBar(requireContext(), "Updating... please wait")
        { probar ->
            // make a reference to your online database and store the image
            val refrencepath =
                storage_refrence.child("customerimage/" + UUID.randomUUID().toString())
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
                                phonenumber = id_et_signin_phonenumber.text.toString()
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

    private fun updateAllUserData(username: String, password: String) {
        Tool.popUpDisplay(requireContext())
        {popup ->
            popup.setTitle("Update in Progress")
            popup.setMessage(" Do you want to update your profile")
            popup.setPositiveButton("Update"){_,_ ->
                Tool.loadingProgressBar(requireContext(), "Updating Profile")
                { probar ->
                    val refrencepath =
                        storage_refrence.child("customerimage/" + UUID.randomUUID().toString())
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
        val oldpath = user_type + "/" + user_type + "_" + user_name + "_" + user_password
        val path = user_type + "/" + user_type + "_" + username + "_" + password
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

                ))
                deleteCurrentUser()
                val email = Constant.getCustomerEmailHack(username)
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
                                "Cannot update Customer")
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

    private fun cancelButtonClicked() {
        id_btn_cancel.setOnClickListener{
            requireActivity().finish()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            Toast.makeText(activity, "profile not updated", Toast.LENGTH_SHORT).show()

        }
    }

    private fun imageClickListener() {
        id_image_profile.setOnClickListener {
            Tool.popUpTitle(requireContext(), "Get Image From")
            { popup ->
                popup.setPositiveButton("gallery") { _, _ ->
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
//                startActivityForResult(intent, Constant.REQUEST_IMAGE_GALLERY)
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        Constant.REQUEST_IMAGE_GALLERY)
                }
                popup.setNegativeButton("camera") { _, _ ->
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        activity?.let {
                            takePictureIntent.resolveActivity(it.packageManager)?.also {
                                val permission = ContextCompat.checkSelfPermission(requireContext(),
                                    Manifest.permission.CAMERA)
                                if (permission != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                                        Manifest.permission.CAMERA), 1)
                                } else {
                                    startActivityForResult(takePictureIntent,
                                        Constant.REQUEST_IMAGE_CAMERA)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getImageUri(cont: Context, bitm: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitm.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(cont.contentResolver, bitm,"title", null)
        return Uri.parse(path)
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
}