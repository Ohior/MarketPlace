package com.example.marketplace

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.example.marketplace.data.UserDatabase
import com.example.marketplace.data.VendorDataClass
import com.example.marketplace.market.MarketActivity
import com.example.marketplace.profile.ProfileActivity
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.tool.Tool
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var id_et_login_name: EditText
    private lateinit var id_et_login_password: EditText
    private lateinit var id_btn_login: Button
    private lateinit var id_btn_customer: Button
    private lateinit var id_tv_create_store: TextView
    private lateinit var id_tv_create_customer: TextView
    private lateinit var id_iv_image: ImageView

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

        //find  view
        id_et_login_name = view.findViewById(R.id.id_et_login_name)
        id_et_login_password = view.findViewById(R.id.id_et_login_password)
        id_btn_login = view.findViewById(R.id.id_btn_login)
        id_btn_customer = view.findViewById(R.id.id_btn_customer)
        id_tv_create_store = view.findViewById(R.id.id_tv_create_store)
        id_tv_create_customer = view.findViewById(R.id.id_tv_create_customer)
        id_iv_image = view.findViewById(R.id.id_iv_image)



        //login button clicked
        createVendorClicked(view)
        createCustomerClicked(view)
        logInBtnClicked()
        customerLogInBtnClicked()

        return view
    }

    private fun customerLogInBtnClicked() {
        id_btn_customer.setOnClickListener {

        Tool.spinAnimation(id_iv_image, 5000)
        //show loading progress bar
        try {
            val username = id_et_login_name.text.toString()
            val password = id_et_login_password.text.toString()
            //logic for user login
            firebase_auth.signInWithEmailAndPassword("customer_"+username+Constant.EMAIL_HACK, password)
                .addOnCompleteListener(this.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        user_database.deleteDatabaseTable()
                        user_name = username
                        user_password = password
                        user_type = "customer"
                        Tool.spinAnimation(id_iv_image, 5000).cancel()
                        storeUserData()
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

    private fun createCustomerClicked(view: View) {
        id_tv_create_customer.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.loginFragment_to_createCustomerFragment)
        }
    }

    private fun logInBtnClicked() {
        id_btn_login.setOnClickListener{
            Tool.spinAnimation(id_iv_image, 5000)
            //show loading progress bar
            try {
                val username = id_et_login_name.text.toString()
                val password = id_et_login_password.text.toString()
                //logic for user login
                firebase_auth.signInWithEmailAndPassword(username+Constant.EMAIL_HACK, password)
                    .addOnCompleteListener(this.requireActivity()) { task ->
                        if (task.isSuccessful) {
                            user_name = username
                            user_password = password
                            user_type = "customer"
                            Tool.spinAnimation(id_iv_image, 5000).cancel()
                            //code for logging in user
                            //save user details
                            id_et_login_name.text.clear()
                            id_et_login_password.text.clear()
                            storeUserData()
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
    private fun createVendorClicked(view: View) {
        id_tv_create_store.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.loginFragment_to_createVendorFragment)
        }
    }

    private fun storeUserData(){
        val firebasemanager = FirebaseManager(requireContext(), requireActivity())
        firebasemanager.getVendorData(
            Constant.VENDOR+"/"+"vendor_"+user_name+"_"+user_password+"/"+user_name){
            user_database.insertIntoDatabase(VendorDataClass(
                it.imguri,
                it.username,
                it.password,
                it.phonenumber,
                it.storename,
                it.address,
                it.longitude,
                it.latitude
            ))
        }
        Tool.debugMessage(user_database.getFromDataBase(0).toString(), "DATABASE")
    }
}
