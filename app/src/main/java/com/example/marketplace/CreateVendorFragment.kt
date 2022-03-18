package com.example.marketplace

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.marketplace.constant.Constant
import com.example.marketplace.dataclass.VendorDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CreateVendorFragment : Fragment() {
    lateinit var id_et_signin_name: EditText
    lateinit var id_et_signin_password: EditText
    lateinit var id_et_signin_phonenumber: EditText
    lateinit var id_et_signin_storename: EditText
    lateinit var id_et_signin_address: EditText
    lateinit var id_image_profile: ImageView
    lateinit var id_btn_cancel: Button
    lateinit var id_btn_sign_in: Button
    private lateinit var profile_image : Bitmap

    private lateinit var firebase_auth: FirebaseAuth
    private lateinit var db_reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initialize firebase
        firebase_auth = FirebaseAuth.getInstance()
        profile_image  = BitmapFactory.decodeResource(resources, R.drawable.applogo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK  && data != null){
            id_image_profile.setImageURI(data.data)
        }else if (requestCode == Constant.REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK  && data != null){
            id_image_profile.setImageBitmap(data.extras?.get("data") as Bitmap)
            profile_image = data.extras?.get("data") as Bitmap
        }else{
            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_vendor, container, false)

        id_et_signin_name = view.findViewById(R.id.id_et_signin_name)
        id_et_signin_password = view.findViewById(R.id.id_et_signin_password)
        id_et_signin_phonenumber  = view.findViewById(R.id.id_et_signin_phonenumber)
        id_et_signin_storename = view.findViewById(R.id.id_et_signin_store_name)
        id_et_signin_address = view.findViewById(R.id.id_et_signin_address)
        id_btn_cancel = view.findViewById(R.id.id_btn_cancel)
        id_btn_sign_in = view.findViewById(R.id.id_btn_sign_in)
        id_image_profile = view.findViewById(R.id.id_image_profile)

        imageClicked()
        buttonCancel(view)
        buttonSignIn(view)

        return view
    }

    private fun imageClicked() {
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
            val name = id_et_signin_name.text.toString()
            val password = id_et_signin_password.text.toString()
            val phonenumber = id_et_signin_phonenumber.text.toString()
            val storename = id_et_signin_storename.text.toString()
            val address = id_et_signin_address.text.toString()
            signInUser(profile_image, name, password, phonenumber, storename, address, view)
        }

    }

    private fun signInUser(profileimg: Bitmap, username: String, password: String,
                           phonenumber: String,storename: String, address: String,
                           view: View
    ) {
        //logic of creating user
        firebase_auth.createUserWithEmailAndPassword(
            username+Constant.EMAIL_HACK, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    addVendorToDatabase(profileimg,username,password,phonenumber,storename,address)
                    Navigation.findNavController(view).navigate(R.id.createVendorFragment_to_loginFragment)
                } else {
                    id_et_signin_name.text.clear()
                    id_et_signin_password.text.clear()
                    id_et_signin_phonenumber.text.clear()
                    id_et_signin_storename.text.clear()
                    id_et_signin_address.text.clear()
                    Toast.makeText(requireContext(), "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun buttonCancel(view: View) {
        id_btn_cancel.setOnClickListener {
            id_et_signin_name.text.clear()
            id_et_signin_password.text.clear()
            id_et_signin_phonenumber.text.clear()
            id_et_signin_storename.text.clear()
            id_et_signin_address.text.clear()
            Navigation.findNavController(view).navigate(R.id.createVendorFragment_to_loginFragment)
            Toast.makeText(activity, "profile not updated", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addVendorToDatabase(profileimg: Bitmap, username: String,
                                    password: String, phonenumber: String,
                                    storename: String, address: String,
    ) {
        db_reference  = FirebaseDatabase.getInstance().getReference()
        //add directory and data to database
        db_reference.child("vendor")
            .child("${username}_${password}")
            .child(username)
            .setValue(
                VendorDataClass(profileimg, username, password, phonenumber, storename, address))
        id_et_signin_name.text.clear()
        id_et_signin_password.text.clear()
        id_et_signin_phonenumber.text.clear()
        id_et_signin_storename.text.clear()
        id_et_signin_address.text.clear()
    }
}
