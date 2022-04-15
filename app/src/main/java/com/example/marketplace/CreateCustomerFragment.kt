package com.example.marketplace

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.marketplace.data.CustomerDataClass
import com.example.marketplace.tool.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class CreateCustomerFragment : Fragment() {
    private lateinit var screen_view: View
    lateinit var id_et_signin_name: EditText
    lateinit var id_et_signin_password: EditText
    lateinit var id_et_signin_phonenumber: EditText
    lateinit var id_image_profile: ImageView
    lateinit var id_btn_cancel: Button
    lateinit var id_btn_sign_in: Button
    private lateinit var image_file_uri : Uri

    private lateinit var firebase_storage: FirebaseStorage
    private lateinit var storage_refrence: StorageReference
    private lateinit var db_refrence: DatabaseReference
    private lateinit var firebase_auth: FirebaseAuth




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null){
            image_file_uri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image_file_uri)
            id_image_profile.setImageBitmap(bitmap)
        }else if (requestCode == Constant.REQUEST_IMAGE_CAMERA && resultCode == Activity.RESULT_OK &&
            data != null){
            val photo = data.extras?.get("data") as Bitmap
            image_file_uri = getImageUri(this.requireContext(), photo)
//            val finalfile = File(getRealPathFromUri(image_file_uri))
            id_image_profile.setImageBitmap(photo)
        }else Toast.makeText(requireContext(), "Could not get image", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase_auth = FirebaseAuth.getInstance()
        firebase_storage = FirebaseStorage.getInstance()
        storage_refrence = firebase_storage.getReference("images")
        db_refrence = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view = inflater.inflate(R.layout.fragment_create_customer, container, false)

        id_et_signin_name = screen_view.findViewById(R.id.id_et_signin_name)
        id_et_signin_password = screen_view.findViewById(R.id.id_et_signin_password)
        id_et_signin_phonenumber  = screen_view.findViewById(R.id.id_et_signin_phonenumber)
        id_btn_cancel = screen_view.findViewById(R.id.id_btn_cancel)
        id_btn_sign_in = screen_view.findViewById(R.id.id_btn_sign_in)
        id_image_profile = screen_view.findViewById(R.id.id_image_profile)

        imageClicked(screen_view)
        buttonCancel(screen_view)
        buttonSignIn(screen_view)

        return screen_view.rootView
    }

        private fun imageClicked(view: View) {
        id_image_profile.setOnClickListener{v ->
        val popup = AlertDialog.Builder(requireContext())
        popup.setTitle("Get Image From")
        popup.setPositiveButton("gallery") { dialog: DialogInterface, which: Int ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, Constant.REQUEST_IMAGE_GALLERY)
        }
        popup.setNegativeButton("camera") { dialog: DialogInterface, which: Int ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                activity?.let {
                    takePictureIntent.resolveActivity(it.packageManager)?.also {
                        val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        if (permission != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 1)
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

    private fun buttonSignIn(view: View) {
        id_btn_sign_in.setOnClickListener {
            signInUser()
        }

    }

    private fun signInUser() {
        //logic of creating user
        val username = id_et_signin_name.text.toString().trim()
        val password = id_et_signin_password.text.toString().trim()
        val phonenumber = id_et_signin_phonenumber.text.toString().trim()
        if (username.contains("_") || password.isEmpty() || username.isEmpty()) {
            Tool.showShortToast(requireContext(), "$username cannot contain underscore")
            return
        }
        Tool.loadingProgressBar(
            requireContext(),
            "Creating Customer... Please wait"
        ){ probar ->
            val refrencepath = storage_refrence.child("vendorimage/"+ UUID.randomUUID().toString())
            refrencepath.putFile(image_file_uri)
                .addOnSuccessListener {
                    refrencepath.downloadUrl.addOnSuccessListener {
                        image_file_uri = it
//                        //put the path in storage
//                        Constant.setString(requireContext(), Constant.CUSIMAGEURI, it.toString())
                    }
                    createCustomer(username, password, phonenumber)
                    probar.dismiss()
                }
                .addOnFailureListener{
                    probar.dismiss()
                    Toast.makeText(this.context, "Failed! could not upload image", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener{

                }
        }
    }

    private fun createCustomer(username: String, password: String, phonenumber: String) {
        firebase_auth.createUserWithEmailAndPassword("customer_"+Constant.getEmailHack(username), password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    addVendor(username, password, phonenumber)
                    Navigation.findNavController(screen_view)
                        .navigate(R.id.createCustomerFragment_to_loginFragment)
                    Toast.makeText(this.context, "Upload Successful", Toast.LENGTH_LONG).show()
                }
                Navigation.findNavController(screen_view)
                    .navigate(R.id.createCustomerFragment_to_loginFragment)
                Constant.showShortToast(requireContext(), "Cannot create Vendor! try another name")
            }
    }

    private fun addVendor(username: String, password: String, phonenumber: String) {
        db_refrence.push()
        db_refrence.child(Constant.CUSTOMER)
            .child("customer_"+username+"_"+password)
            .child(username)
            .setValue(CustomerDataClass(
                image_file_uri.toString(),
                username,
                password,
                phonenumber)
            )
    }

    private fun buttonCancel(view: View) {
        id_btn_cancel.setOnClickListener { v ->
            Navigation.findNavController(view).navigate(R.id.createCustomerFragment_to_loginFragment)
            Toast.makeText(activity, "profile not updated", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getImageUri(cont: Context, bitm:Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitm.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(cont.contentResolver, bitm,"title", null)
        return Uri.parse(path)
    }
}