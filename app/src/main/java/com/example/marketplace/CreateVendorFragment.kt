package com.example.marketplace


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.example.marketplace.tool.Constant
import com.example.marketplace.data.VendorDataClass
import com.google.firebase.auth.FirebaseAuth
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.tool.Tool



class CreateVendorFragment : Fragment() {
    private lateinit var screen_view: View
    private lateinit var id_et_signin_name: EditText
    private lateinit var id_et_signin_password: EditText
    private lateinit var id_et_signin_phonenumber: EditText
    private lateinit var id_et_signin_storename: EditText
    private lateinit var id_btn_cancel: Button
    private lateinit var id_btn_sign_up: Button


//    private lateinit var image_file_uri: Uri
//    private lateinit var firebase_storage: FirebaseStorage
//    private lateinit var storage_refrence: StorageReference
//    private lateinit var db_refrence: DatabaseReference
    private lateinit var firebase_auth: FirebaseAuth

    private lateinit var firebase_manager: FirebaseManager



//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK &&
//                data != null && data.data != null){
//            image_file_uri = data.data!!
//            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image_file_uri)
//            id_image_profile.setImageBitmap(bitmap)
//        }else if (requestCode == Constant.REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK &&
//                data != null){
//            val photo = data.extras?.get("data") as Bitmap
//            image_file_uri = getImageUri(this.requireContext(), photo)
//            val finalfile = File(getRealPathFromUri(image_file_uri))
//            id_image_profile.setImageBitmap(photo)
//        }else Toast.makeText(requireContext(), "Could not get image", Toast.LENGTH_LONG).show()
//    }

//    private fun getRealPathFromUri(uri: Uri): String {
//        var path = ""
//        if (requireActivity().contentResolver != null){
//            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
//            if(cursor != null){
//                cursor.moveToFirst()
//                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//                path = cursor.getString(idx)
//                cursor.close()
//            }
//        }
//        return path
//    }


//    private fun getImageUri(cont: Context, bitm:Bitmap): Uri {
//        val bytes = ByteArrayOutputStream()
//        bitm.compress(Bitmap.CompressFormat.PNG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(cont.contentResolver, bitm,"title", null)
//        return Uri.parse(path)
//    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == Constant.REQUEST_IMAGE_CAMERA)
//        {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(cameraIntent, Constant.REQUEST_IMAGE_CAMERA)
//            }else{
//                Toast.makeText(requireContext(), "camera permission denied", Toast.LENGTH_LONG).show()
//            }
//        }
//    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase_manager = FirebaseManager(requireContext(), requireActivity())
        firebase_auth = FirebaseAuth.getInstance()
//        firebase_storage = FirebaseStorage.getInstance()
//        storage_refrence = firebase_storage.getReference("images")
//        db_refrence = FirebaseDatabase.getInstance().reference
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_vendor, container, false)
        screen_view = view
        //initialise the views
        id_et_signin_name = view.findViewById(R.id.id_et_signin_name)
        id_et_signin_password = view.findViewById(R.id.id_et_signin_password)
        id_et_signin_phonenumber = view.findViewById(R.id.id_et_signin_phonenumber)
        id_et_signin_storename = view.findViewById(R.id.id_et_signin_store_name)
        id_btn_cancel = view.findViewById(R.id.id_btn_cancel)
        id_btn_sign_up = view.findViewById(R.id.id_btn_sign_up)

//        imageClickListener()
        signUpButtonClickListener()
        cancelButtonClicked(view)

        return view
    }

    private fun cancelButtonClicked(view: View) {
        id_btn_cancel.setOnClickListener { v ->
            Navigation.findNavController(view).navigate(R.id.createVendorFragment_to_loginFragment)
            Toast.makeText(activity, "profile not updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUpButtonClickListener() {
        id_btn_sign_up.setOnClickListener{
            uploadAndCreateVendor()
        }
    }

    private fun uploadAndCreateVendor() {
        val name = id_et_signin_name.text.toString().trim()
        val password = id_et_signin_password.text.toString().trim()
        val phonenumber = id_et_signin_phonenumber.text.toString().trim()
        val storename = id_et_signin_storename.text.toString().trim()
        if (password.isEmpty() || name.isEmpty() || phonenumber.isEmpty() || storename.isEmpty()){
            Tool.showShortToast(requireContext(), "Do not leave any Field empty")
            return
        }
        else if (name.contains("_")){
            Tool.showShortToast(requireContext(), "Do not add underscore to name")
            return
        }
        Tool.loadingProgressBar(requireContext(), message = "Creating Profile please wait...")
        {probar ->
            val path = Constant.VENDOR+"/"+Constant.VENDOR+"_"+name+"_"+password+"/"+"user"
            firebase_auth.createUserWithEmailAndPassword(Constant.getVendorEmailHack(name), password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        probar.dismiss()
                        firebase_manager.addToFirebaseDatabase(path,
                            VendorDataClass(
                                username = name,
                                password = password,
                                phonenumber = phonenumber,
                                storename = storename
                            )
                        )
                        Tool.showShortToast(requireContext(), "Upload Successful")
                        Navigation.findNavController(screen_view)
                            .navigate(R.id.createVendorFragment_to_loginFragment)
                    }
                    else{
                        probar.dismiss()
                        Tool.showShortToast(requireContext(), "Cannot create Vendor")
                    }
            }
        }
    }
}

//    private fun getFileExtension(uri: Uri): String? {
//        val cr = context?.contentResolver
//        val mime = MimeTypeMap.getSingleton()
//        return mime.getExtensionFromMimeType(cr?.getType(uri))
//    }
//
//    private fun uploadImageAndCreateVendor(){
//        val username = id_et_signin_name.text.toString()
//        val password = id_et_signin_password.text.toString()
//        if (username.contains("_")) {
//            Tool.showShortToast(requireContext(), "$username cannot contain underscore")
//            return
//        }
//        Tool.loadingProgressBar(
//            requireContext(),
//            "Creating vendor... Please wait"
//        ) { probar ->
//            val refrencepath = storage_refrence.child("vendorimage/"+ UUID.randomUUID().toString())
//            refrencepath.putFile(image_file_uri)
//                .addOnSuccessListener {
//                    refrencepath.downloadUrl.addOnSuccessListener {
//                        image_file_uri = it
//                        //put the path in storage
//                        Constant.setString(requireContext(), Constant.IMAGE_URI, it.toString())
//                    }
//                    createVendor(username, password)
//                    probar.dismiss()
//                }
//                .addOnFailureListener{
//                    Toast.makeText(this.context, "Failed! could not upload image", Toast.LENGTH_SHORT).show()
//                    probar.dismiss()
//                }
//                .addOnProgressListener{
//
//                }
//        }
//
//    }
//
////    private fun imageClickListener() {
////        id_image_profile.setOnClickListener{
////            val popup = AlertDialog.Builder(requireContext())
////            popup.setTitle("Get Image From")
////            popup.setPositiveButton("gallery") { _: DialogInterface, _: Int ->
////                val intent = Intent(Intent.ACTION_GET_CONTENT)
////                intent.type = "image/*"
//////                startActivityForResult(intent, Constant.REQUEST_IMAGE_GALLERY)
////                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constant.REQUEST_IMAGE_GALLERY)
////            }
////            popup.setNegativeButton("camera") { _: DialogInterface, _: Int ->
//////                        takePicture()
////                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
////                    activity?.let {
////                        takePictureIntent.resolveActivity(it.packageManager)?.also {
////                            val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
////                            if (permission != PackageManager.PERMISSION_GRANTED){
////                                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 1)
////                            }else{
////                                startActivityForResult(takePictureIntent, Constant.REQUEST_IMAGE_CAMERA)
////                            }
////                        }
////                    }
////                }
////            }
////            popup.show()
////        }
////    }
//
//    private fun createVendor(username:String, password:String) {
//        //initialize the rest of the view
//        val phonenumber = id_et_signin_phonenumber.text.toString()
//        val storename = id_et_signin_storename.text.toString()
//
////        if (Constant.checkVendorEmptyFields(VendorDataClass(image_file_uri.toString(),
////            username, password, phonenumber, storename, address))) {
//            firebase_auth.createUserWithEmailAndPassword(Constant.getEmailHack(username), password)
//                .addOnCompleteListener(requireActivity()) { task ->
//                    if (task.isSuccessful) {
//                        addVendor(username, password, phonenumber, storename)
//                        Navigation.findNavController(screen_view)
//                            .navigate(R.id.createVendorFragment_to_loginFragment)
//                        Toast.makeText(this.context, "Upload Successful", Toast.LENGTH_LONG).show()
//                    }
//                    Navigation.findNavController(screen_view)
//                        .navigate(R.id.createVendorFragment_to_loginFragment)
//                    Constant.showShortToast(requireContext(), "Cannot create Vendor")
//                }
////        }
//    }
//
//    private fun addVendor(username: String, password: String, phonenumber: String, storename: String) {
//        db_refrence.push()
//        db_refrence.child(Constant.VENDOR)
//            .child("vendor_"+username+"_"+password)
//            .child(username)
//            .setValue(VendorDataClass(
//                imguri = image_file_uri.toString(),
//                username = username,
//                password = password,
//                phonenumber = phonenumber,
//                storename = storename)
//            )
//    }
//
//}