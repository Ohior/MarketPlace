package com.example.marketplace

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.example.marketplace.data.CustomerDataClass
import com.example.marketplace.data.UserDatabase
import com.example.marketplace.data.VendorDataClass
import com.example.marketplace.market.MarketActivity
import com.example.marketplace.profile.ProfileActivity
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.tool.Tool
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var screen_view : View
    private lateinit var id_et_login_name: EditText
    private lateinit var id_et_login_password: EditText
    private lateinit var id_btn_login: Button
    private lateinit var id_btn_customer: Button
    private lateinit var id_tv_create_store: TextView
    private lateinit var id_tv_create_customer: TextView
    private lateinit var id_iv_image: ImageView
    private lateinit var id_iv_show_password: Button
    private lateinit var id_btn_visitor: Button

    private lateinit var firebase_auth: FirebaseAuth
    private lateinit var firebase_manager: FirebaseManager

    private lateinit var user_name:String
    private lateinit var user_password:String
    private lateinit var user_type:String

    private lateinit var  user_database: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase_auth = FirebaseAuth.getInstance()
        firebase_manager = FirebaseManager(requireContext(), requireActivity())
        user_database = UserDatabase(requireContext(), Constant.USER_DB_NAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        screen_view = view
        //find  view
        id_et_login_name = view.findViewById(R.id.id_et_login_name)
        id_et_login_password = view.findViewById(R.id.id_et_login_password)
        id_btn_login = view.findViewById(R.id.id_btn_login)
        id_btn_customer = view.findViewById(R.id.id_btn_customer)
        id_tv_create_store = view.findViewById(R.id.id_tv_create_store)
        id_tv_create_customer = view.findViewById(R.id.id_tv_create_customer)
        id_iv_image = view.findViewById(R.id.id_iv_image)
        id_iv_show_password = view.findViewById(R.id.id_iv_show_password)
        id_btn_visitor = view.findViewById(R.id.id_btn_visitor)



        //login button clicked
        createVendorClicked()
        createCustomerClicked()
        vendorLogInBtnClicked()
        customerLogInBtnClicked()
        exploreButtonClicked()
        // Toggle password text
        id_iv_show_password.setOnClickListener {
            if (id_iv_show_password.text.equals("Show")){
//                id_et_login_password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                id_et_login_password.transformationMethod = null
                id_iv_show_password.text = "Hide"
            }else{
                id_et_login_password.transformationMethod = PasswordTransformationMethod()
                id_iv_show_password.text = "Show"
            }
        }


        return view
    }

    private fun exploreButtonClicked() {
        id_btn_visitor.setOnClickListener {
            startActivity(Intent(requireContext(), MarketActivity::class.java))
        }
    }

    private fun customerLogInBtnClicked() {
        id_btn_customer.setOnClickListener {

        Tool.spinAnimation(id_iv_image, 5000)
        //show loading progress bar
        try {
            val username = id_et_login_name.text.toString()
            val password = id_et_login_password.text.toString()
            //logic for user login
            firebase_auth.signInWithEmailAndPassword(Constant.getCustomerEmailHack(username), password)
                .addOnCompleteListener(this.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        user_database.deleteDatabaseTable()
                        user_name = username
                        user_password = password
                        user_type = "customer"
                        Tool.spinAnimation(id_iv_image, 5000).cancel()
                        storeUserToLocalDatabase()
                        //save user details
                        id_et_login_name.text.clear()
                        id_et_login_password.text.clear()
                        Constant.setString(requireContext(), Constant.USERNAME, username)
                        Constant.setString(requireContext(), Constant.PASSWORD, password)
                        Constant.setString(requireContext(), Constant.USER_TYPE, "customer")
                        startActivity(Intent(context, MarketActivity::class.java))

                    } else {
                        Tool.spinAnimation(id_iv_image, 5000).cancel()
                        id_et_login_name.text.clear()
                        id_et_login_password.text.clear()
                        Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                    }
                }
        }catch (e:IllegalArgumentException){
            Tool.spinAnimation(id_iv_image, 5000).cancel()
            Toast.makeText(context,"All fields should be filled",Toast.LENGTH_SHORT).show()
        }
        }
    }

    private fun createCustomerClicked() {
        id_tv_create_customer.setOnClickListener{
            Tool.popUpWindow(requireActivity(), "Create Customer",R.layout.create_user_popup)
            {view ->
                val create = view.findViewById<Button>(R.id.id_btn_sign_up)
                val storename = view.findViewById<EditText>(R.id.id_et_signin_store_name)
                storename.visibility = EditText.GONE
                create.text = "Create Customer"
                create.setOnClickListener {
                    val name = view.findViewById<EditText>(R.id.id_et_signin_name).text.toString()
                    val password = view.findViewById<EditText>(R.id.id_et_signin_password).text.toString()
                    createCustomer(name, password)
                }
            }
            //Navigation.findNavController(view).navigate(R.id.loginFragment_to_createCustomerFragment)
        }
    }

    private fun createCustomer(name: String, password: String) {
        if (password.isEmpty() || name.isEmpty()){
            Tool.showShortToast(requireContext(), "Do not leave any Field empty")
            return
        }
        else if (name.contains("_")){
            Tool.showShortToast(requireContext(), "Do not add underscore to name")
            return
        }
        Tool.loadingProgressBar(requireContext(), message = "Creating Profile please wait..."){probar ->
            val path = Constant.CUSTOMER+"/"+Constant.CUSTOMER+"_"+name+"_"+password+"/"+"user"+"/"
            firebase_auth.createUserWithEmailAndPassword(Constant.getCustomerEmailHack(name), password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        probar.dismiss()
                        firebase_manager.addToFirebaseDatabase(path, CustomerDataClass(
                            username = name, password = password)
                        )
                        Navigation.findNavController(screen_view)
                            .navigate(R.id.createCustomerFragment_to_loginFragment)
                        Tool.showShortToast(requireContext(), "Upload Successful")
                    }
                    else{
                        probar.dismiss()
                        Tool.showShortToast(requireContext(), "Cannot create customer")
                    }
                }
        }
    }

    private fun vendorLogInBtnClicked() {
        id_btn_login.setOnClickListener{
            Tool.spinAnimation(id_iv_image, 5000)
            //show loading progress bar
            try {
                val username = id_et_login_name.text.toString()
                val password = id_et_login_password.text.toString()
                //logic for user login
                firebase_auth.signInWithEmailAndPassword(Constant.getVendorEmailHack(username), password)
                    .addOnCompleteListener(this.requireActivity()) { task ->
                        if (task.isSuccessful) {
                            user_database.deleteDatabaseTable()
                            user_name = username
                            user_password = password
                            user_type = "customer"
                            Tool.spinAnimation(id_iv_image, 5000).cancel()
                            //code for logging in user
                            //save user details
                            id_et_login_name.text.clear()
                            id_et_login_password.text.clear()
                            storeUserToLocalDatabase()
                            Constant.setString(requireContext(), Constant.USERNAME, username)
                            Constant.setString(requireContext(), Constant.PASSWORD, password)
                            Constant.setString(requireContext(), Constant.USER_TYPE, "vendor")
                            startActivity(Intent(context, ProfileActivity::class.java))

                        } else {
                            Tool.spinAnimation(id_iv_image, 5000).cancel()
                            id_et_login_name.text.clear()
                            id_et_login_password.text.clear()
                            Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                        }
                    }
            }catch (e:IllegalArgumentException){
                Tool.spinAnimation(id_iv_image, 5000).cancel()
                Toast.makeText(context,"All fields should be filled",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createVendorClicked() {
        id_tv_create_store.setOnClickListener{

        Tool.popUpWindow(requireActivity(), "Create Vendor",R.layout.create_user_popup)
        {view ->
            val create = view.findViewById<Button>(R.id.id_btn_sign_up)
            create.setOnClickListener {
                val name = view.findViewById<EditText>(R.id.id_et_signin_name).text.toString()
                val password = view.findViewById<EditText>(R.id.id_et_signin_password).text.toString()
                val storename = view.findViewById<EditText>(R.id.id_et_signin_store_name).text.toString()
                uploadAndCreateVendor(name, password, storename)
                }
            }
        }
            //Navigation.findNavController(view).navigate(R.id.loginFragment_to_createVendorFragment)
    }

    private fun uploadAndCreateVendor(name: String, password: String, storename: String) {

        if (password.isEmpty() || name.isEmpty() || storename.isEmpty()){
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

    private fun storeUserToLocalDatabase(){
        val firebasemanager = FirebaseManager(requireContext(), requireActivity())
        firebasemanager.getVendorData(
            Constant.VENDOR+"/"+"vendor_"+user_name+"_"+user_password+"/"+"user"){
            user_database.insertIntoDatabase(VendorDataClass(
                imguri = it.imguri,
                username = it.username,
                password = it.password,
                phonenumber = it.phonenumber,
                storename = it.storename,
                address = it.address,
                longitude = it.longitude,
                latitude = it.latitude
            ))
        }
    }
}
