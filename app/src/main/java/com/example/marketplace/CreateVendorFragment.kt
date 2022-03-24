package com.example.marketplace

import android.Manifest
import android.R.attr
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.marketplace.shop.ShopActivity
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.VendorDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.R.attr.data
import android.content.Context
import android.media.Image
import java.io.ByteArrayOutputStream
import java.io.File


class CreateVendorFragment : Fragment() {
    private lateinit var layout_view: View
    lateinit var id_et_signin_name: EditText
    lateinit var id_et_signin_password: EditText
    lateinit var id_et_signin_phonenumber: EditText
    lateinit var id_et_signin_storename: EditText
    lateinit var id_et_signin_address: EditText
    lateinit var id_image_profile: ImageView
    lateinit var id_btn_cancel: Button
    lateinit var id_btn_sign_up: Button


    private lateinit var image_file_uri: Uri
    private lateinit var firebase_storage: FirebaseStorage
    private lateinit var storage_refrence: StorageReference
    private lateinit var db_refrence: DatabaseReference
    private lateinit var firebase_auth: FirebaseAuth



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK &&
                data != null && data.data != null){
            image_file_uri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image_file_uri)
            id_image_profile.setImageBitmap(bitmap)
        }else if (requestCode == Constant.REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK &&
                data != null){
            val photo = data.extras?.get("data") as Bitmap
            image_file_uri = getImageUri(this.requireContext(), photo)
            val finalfile = File(getRealPathFromUri(image_file_uri))
            id_image_profile.setImageBitmap(photo)
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


    private fun getImageUri(cont: Context, bitm:Bitmap): Uri {
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
                val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, Constant.REQUEST_IMAGE_CAMERA);
            }else{
                Toast.makeText(requireContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
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
        val view = inflater.inflate(R.layout.fragment_create_vendor, container, false)
        layout_view = view
        //initialise the views
        id_et_signin_name = view.findViewById(R.id.id_et_signin_name)
        id_et_signin_password = view.findViewById(R.id.id_et_signin_password)
        id_et_signin_phonenumber = view.findViewById(R.id.id_et_signin_phonenumber)
        id_et_signin_storename = view.findViewById(R.id.id_et_signin_store_name)
        id_et_signin_address = view.findViewById(R.id.id_et_signin_address)
        id_btn_cancel = view.findViewById(R.id.id_btn_cancel)
        id_btn_sign_up = view.findViewById(R.id.id_btn_sign_up)
        id_image_profile = view.findViewById(R.id.id_image_profile)

        imageClickListener()
        signUpButtonClickListener()

        return view
    }

    private fun signUpButtonClickListener() {
        id_btn_sign_up.setOnClickListener {
            uploadImageAndCreateVendor()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cr = context?.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr?.getType(uri))
    }

    private fun uploadImageAndCreateVendor(){
        val username = id_et_signin_name.text.toString()
        val password = id_et_signin_password.text.toString()
//        val refrence = storage_refrence.child(System.currentTimeMillis().toString()+"."+getFileExtension(image_file_uri))
        val imgfilepath = storage_refrence.child(image_file_uri.lastPathSegment!!)
//        upload_task = refrence.putFile(file_path)
        imgfilepath.putFile(image_file_uri)
            .addOnSuccessListener {
                imgfilepath.downloadUrl.addOnSuccessListener {
                    // it contains the online file path
                    image_file_uri = it
                    //put the path in storage
                    Constant.setString(requireContext(), Constant.IMAGEURI, it.toString())
                }
                createVendor(username, password)
                Toast.makeText(this.context, "Upload Successful", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener {
            }
    }

    private fun imageClickListener() {
        id_image_profile.setOnClickListener{v ->
            val popup = AlertDialog.Builder(requireContext())
            popup.setTitle("Get Image From")
            popup.setPositiveButton("gallery") { dialog: DialogInterface, which: Int ->
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
//                startActivityForResult(intent, Constant.REQUEST_IMAGE_GALLERY)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constant.REQUEST_IMAGE_GALLERY)
            }
            popup.setNegativeButton("camera") { dialog: DialogInterface, which: Int ->
//                        takePicture()
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

    private fun createVendor(username:String, password:String) {
        //initialize the rest of the view
        val phonenumber = id_et_signin_phonenumber.text.toString()
        val storename = id_et_signin_storename.text.toString()
        val address = id_et_signin_address.text.toString()

        if (Constant.checkVendorEmptyFields(VendorDataClass(image_file_uri.toString(),
            username, password, phonenumber, storename, address)))
        firebase_auth.createUserWithEmailAndPassword(Constant.getEmailHack(username),password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful){
                    addVendor(username, password, phonenumber, storename, address)
                    Navigation.findNavController(layout_view).navigate(R.id.createVendorFragment_to_loginFragment)
                }
                Constant.showShortToast(requireContext(), "Cannot create Vendor")
            }
    }

    private fun addVendor(username: String, password: String, phonenumber: String, storename: String, address: String, ) {
        db_refrence.push()
        db_refrence.child(Constant.VENDOR)
            .child(username+"_"+password)
            .child(username)
            .setValue(VendorDataClass(
                image_file_uri.toString(),
                username,
                password,
                phonenumber,
                storename,
                address)
            )
    }

}