package com.example.marketplace

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.example.marketplace.tool.Constant


class CreateCustomerFragment : Fragment() {
    lateinit var id_et_signin_name: EditText
    lateinit var id_et_signin_password: EditText
    lateinit var id_et_signin_phonenumber: EditText
    lateinit var id_image_profile: ImageView
    lateinit var id_btn_cancel: Button
    lateinit var id_btn_sign_in: Button
    lateinit var profile_image : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_customer, container, false)

        id_et_signin_name = view.findViewById(R.id.id_et_signin_name)
        id_et_signin_password = view.findViewById(R.id.id_et_signin_password)
        id_et_signin_phonenumber  = view.findViewById(R.id.id_et_signin_phonenumber)
        id_btn_cancel = view.findViewById(R.id.id_btn_cancel)
        id_btn_sign_in = view.findViewById(R.id.id_btn_sign_in)
        id_image_profile = view.findViewById(R.id.id_image_profile)

        imageClicked(view)
        buttonCancel(view)
        buttonSignIn(view)

        return view
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
        val name = id_et_signin_name.text.toString()
        val password = id_et_signin_password.text.toString()
        val phonenumber = id_et_signin_phonenumber.text.toString()
        id_btn_sign_in.setOnClickListener { v ->
            signInUser(profile_image, name, password, phonenumber)
        }

    }

    private fun signInUser(
        img: Bitmap,
        name: String,
        password: String,
        phonenumber: String,
    ) {
        //logic of creating user
    }

    private fun buttonCancel(view: View) {
        id_btn_cancel.setOnClickListener { v ->
            Navigation.findNavController(view).navigate(R.id.createCustomerFragment_to_loginFragment)
            Toast.makeText(activity, "profile not updated", Toast.LENGTH_SHORT).show()
        }

    }

}