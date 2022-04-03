package com.example.marketplace

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.example.marketplace.tool.Constant
import com.example.marketplace.shop.ShopActivity
import com.example.marketplace.tool.Tool
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var id_et_login_name: EditText
    private lateinit var id_et_login_password: EditText
    private lateinit var id_btn_login: Button
    private lateinit var id_btn_customer: Button
    private lateinit var id_tv_create_store: TextView
    private lateinit var id_iv_image: ImageView

    private lateinit var firebase_auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase_auth = FirebaseAuth.getInstance()

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
        id_iv_image = view.findViewById(R.id.id_iv_image)



        //login button clicked
        createVendorClicked(view)
        logInBtnClicked()
        customerButtonClicked(view)

        return view
    }

        private fun customerButtonClicked(view: View) {
        id_btn_customer.setOnClickListener {
            //logic for customer login
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
                            //code for logging in user
                            val intent = Intent(context, ShopActivity::class.java)
                            //save user details
                            id_et_login_name.text.clear()
                            id_et_login_password.text.clear()
                            Constant.setString(requireContext(), Constant.USERNAME, username)
                            Constant.setString(requireContext(), Constant.PASSWORD, password)
                            startActivity(intent)
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
}
