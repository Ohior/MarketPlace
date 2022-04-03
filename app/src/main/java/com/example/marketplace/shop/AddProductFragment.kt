package com.example.marketplace.shop

import android.Manifest
import android.app.Activity.RESULT_OK
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
import com.example.marketplace.R
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.ProductDataClass
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class AddProductFragment : Fragment() {

    private lateinit var id_iv_product_img: ImageView
    private lateinit var id_product_item_pname: EditText
    private lateinit var id_product_item_price: EditText
    private lateinit var id_product_item_detail: EditText
    private lateinit var id_btn_cancel: Button
    private lateinit var id_btn_save: Button
    private lateinit var product_image: Bitmap
    private lateinit var product_image_uri: Uri

    private lateinit var db_reference: DatabaseReference
    private lateinit var storage_refrence: StorageReference
    private lateinit var firebase_storage: FirebaseStorage




//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK  && data != null){
//            id_iv_product_img.setImageURI(data.data)
//            product_image = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, data.data);
//        }else if (requestCode == Constant.REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK  && data != null){
//            id_iv_product_img.setImageBitmap(data.extras?.get("data") as Bitmap)
//            product_image = data.extras?.get("data") as Bitmap
//        }else{
//            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK &&
            data != null && data.data != null
        ) {
            product_image_uri = data.data!!
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, product_image_uri)
            id_iv_product_img.setImageBitmap(bitmap)
        } else if (requestCode == Constant.REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK &&
            data != null
        ) {
            val photo = data.extras?.get("data") as Bitmap
            product_image_uri = Constant.getImageUri(this.requireContext(), photo)
            val finalfile = File(Constant.getRealPathFromUri(requireActivity(), requireContext(), product_image_uri))
            id_iv_product_img.setImageBitmap(photo)
        } else Toast.makeText(requireContext(), "Could not get image", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase_storage = FirebaseStorage.getInstance()
        storage_refrence = firebase_storage.getReference("images")
        db_reference  = FirebaseDatabase.getInstance().getReference()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        id_iv_product_img = view.findViewById(R.id.id_iv_product_img)
        id_product_item_pname = view.findViewById(R.id.id_product_item_pname)
        id_product_item_price = view.findViewById(R.id.id_product_item_price)
        id_product_item_detail = view.findViewById(R.id.id_product_item_detail)
        id_btn_cancel = view.findViewById(R.id.id_btn_cancel)
        id_btn_save = view.findViewById(R.id.id_btn_save)


        chooseImage()

        cancelButtonClicked(view)

        saveButtonClicked(view)

        return view
    }

    private fun saveButtonClicked(view: View) {
        id_btn_save.setOnClickListener{
            //get details

            try {
                val imgfilepath = storage_refrence.child(product_image_uri.lastPathSegment!!)
//                product_image_uri.lastPathSegment
                saveProductToDatabase(imgfilepath, view)
            }catch (uninitialized: UninitializedPropertyAccessException){
                product_image_uri = Constant.getDrawableUri(resources, R.drawable.applogo)
                val imgfilepath = storage_refrence.child(product_image_uri.lastPathSegment!!)
                saveProductToDatabase(imgfilepath, view)
            }

//            val imgfilepath = storage_refrence.child(product_image_uri.lastPathSegment!!)
//            saveProductToDatabase(imgfilepath)

//            db_reference  = FirebaseDatabase.getInstance().getReference()
        }
    }

    private fun saveProductToDatabase(imgfilepath: StorageReference, view:View) {
        val pname = id_product_item_pname.text.toString()
        val price = id_product_item_price.text.toString()
        val detail = id_product_item_detail.text.toString()
        if (Constant.checkProductEmptyFields(ProductDataClass(product_image_uri.toString(), pname, price, detail))){
            //check if any field is empty
            imgfilepath.putFile(product_image_uri)
                .addOnSuccessListener {
                    imgfilepath.downloadUrl.addOnSuccessListener {
                        // it contains the online file path
                        product_image_uri = it
                        Constant.debugMessage(product_image_uri.toString(), tag = "product_image_uri")
                        //put the path in storage
                        Constant.setString(requireContext(), Constant.IMAGEURI, it.toString())
                    }
                    addProduct(pname, price, detail)
                    Navigation.findNavController(view).navigate(R.id.addProductFragment_to_shopFragment)
                    Toast.makeText(this.context, "Upload Successful", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                }

        }else Constant.showShortToast(requireContext(), "All fields should be filled :|")

    }

    private fun addProduct(pname: String, price: String, detail: String) {
        //add directory and data to database
        db_reference.push()
        val uname = Constant.getString(requireContext(), Constant.USERNAME)
        val pword = Constant.getString(requireContext(), Constant.PASSWORD)
        db_reference.child("vendor")
            .child("${uname}_${pword}")
            .child(Constant.PRODUCT)
            .child(pname)
            .setValue(ProductDataClass(product_image_uri.toString(), pname, price, detail))
//            .setValue(ProductDataClass(product_image_uri.toString(), pname, price, detail))
        id_product_item_pname.text.clear()
        id_product_item_price.text.clear()
        id_product_item_detail.text.clear()
        Toast.makeText(activity, "Product updated", Toast.LENGTH_SHORT).show()
    }

    private fun cancelButtonClicked(view:View) {
        id_btn_cancel.setOnClickListener {
            id_product_item_pname.text.clear()
            id_product_item_price.text.clear()
            id_product_item_detail.text.clear()
            Navigation.findNavController(view).navigate(R.id.addProductFragment_to_shopFragment)
            Toast.makeText(activity, "Product not updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseImage() {
        id_iv_product_img.setOnClickListener{v ->
        val popup = AlertDialog.Builder(requireContext())
        popup.setTitle("Get Image From")
        popup.setPositiveButton("gallery") { dialog: DialogInterface, which: Int ->
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
//            startActivityForResult(intent, Constant.REQUEST_IMAGE_GALLERY)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constant.REQUEST_IMAGE_GALLERY)

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
}