package com.example.marketplace.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.example.marketplace.adapter.ProductRecyclerAdapter
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.data.ProductDataClass
import com.example.marketplace.data.UserDatabase
import com.example.marketplace.market.MarketActivity
import com.example.marketplace.tool.Tool
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {
    private lateinit var screen_view: View
    //ids
    private lateinit var id_rl_shop_profile: RelativeLayout
    private lateinit var id_fab_add_product: FloatingActionButton
    private lateinit var id_rv_shop_items: RecyclerView
    private lateinit var id_image_profile: ImageView
    private lateinit var id_tv_profile_shop_name: TextView
    private lateinit var id_tv_profile_address: TextView
    private lateinit var id_tv_profile_name: TextView

    //database
    private lateinit var db_reference: DatabaseReference
    private lateinit var firebase_manager: FirebaseManager

    //
    private lateinit var user_name: String
    private lateinit var user_type: String
    private lateinit var user_password: String

    //adapter
    private lateinit var product_recycler_adapter: ProductRecyclerAdapter

    //array list
    private lateinit var product_array_list: ArrayList<ProductDataClass>

    //DATABASE
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
                    "vendor" -> {
                        Navigation.findNavController(screen_view).navigate(R.id.profileFragment_to_chatListFragment2)
                    }
                }
                true
            }
            R.id.id_menu_market ->{
                requireActivity().finish()
                startActivity(Intent(requireContext(), MarketActivity::class.java))
                Toast.makeText(requireContext(), "Market", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.id_menu_location ->{
                Navigation.findNavController(screen_view).navigate(R.id.shopFragment_to_locateFragment)
                true
            }
            else -> {
                //this makes it so menu item works in fragment
                NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                        || super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get default data from user
        user_name = Constant.getString(requireContext(), Constant.USERNAME).toString()
        user_type = Constant.getString(requireContext(), Constant.USER_TYPE).toString()
        user_password = Constant.getString(requireContext(), Constant.PASSWORD).toString()

        //build database
        user_database = UserDatabase(requireContext(), Constant.USER_DB_NAME)

        if(user_type == "customer"){
            Navigation.findNavController(screen_view).navigate(R.id.profileFragment_to_profileEditFragment)
        }


        product_array_list = ArrayList()

        //set firebase for functionality
        db_reference = FirebaseDatabase.getInstance().reference
        firebase_manager = FirebaseManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view = inflater.inflate(R.layout.fragment_profile, container, false)

        //initialize the views
        id_rl_shop_profile = screen_view.findViewById(R.id.id_rl_shop_profile)
        id_fab_add_product = screen_view.findViewById(R.id.id_fab_add_product)
        id_rv_shop_items = screen_view.findViewById(R.id.id_rv_shop_items)
        id_image_profile = screen_view.findViewById(R.id.id_image_profile)
        id_tv_profile_shop_name = screen_view.findViewById(R.id.id_tv_profile_shop_name)
        id_tv_profile_address = screen_view.findViewById(R.id.id_tv_profile_address)
        id_tv_profile_name = screen_view.findViewById(R.id.id_tv_profile_name)

        //create layout for product recycler view
        id_rv_shop_items.layoutManager = GridLayoutManager(context, Constant.COL_COUNT)

        // set the ProductRecyclerAdapter
        product_recycler_adapter = ProductRecyclerAdapter(requireContext(),product_array_list)
        id_rv_shop_items.adapter = product_recycler_adapter

        productRecyclerListener()
        getAndStoreVendorData()
        shopProfileClicked()
        getAndDisplayProductFromDatabase()
        floatingActionButtonClicked()
        Tool.debugMessage("ProfileFragmentClass")

        //configure this fragment for menu item
        setHasOptionsMenu(true)
        return screen_view.rootView
    }

    private fun productRecyclerListener() {
        product_recycler_adapter.setOnItemClickListener(
            object : ProductRecyclerAdapter.OnClickListener {
                override fun onItemClick(position: Int, view: View) {
                    Navigation.findNavController(screen_view)
                        .navigate(R.id.profileFragment_to_profileProductFragment)
                }

                override fun onLongItemClick(position: Int, view: View) {
                    val imguri: ImageView = view.findViewById(R.id.id_iv_product_img)
                    val itemprice: TextView = view.findViewById(R.id.id_store_item_price)
                    val itemproduct: TextView = view.findViewById(R.id.id_store_item_pname)
                    val itemdetali: TextView = view.findViewById(R.id.id_store_item_detail)
                    val popup = AlertDialog.Builder(requireContext())
                    popup.setTitle("Delete $itemproduct")
                    popup.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                        // deleting product from database goes here
                    }
                    popup.setNegativeButton("No", null)
                    popup.show()
                }

            }
        )
    }

    private fun getAndStoreVendorData() {
        firebase_manager.getVendorData(
            Constant.VENDOR+"/"+"vendor_"+user_name+"_"+user_password+"/"+user_name)
        {
            Picasso.get().load(it.imguri).into(id_image_profile)
            id_tv_profile_name.text = it.username
            id_tv_profile_shop_name.text = it.storename
            id_tv_profile_address.text = it.address
            Constant.setString(requireContext(), Constant.USERNAME, it.username)
            Constant.setString(requireContext(), Constant.ADDRESS, it.address)
            Constant.setString(requireContext(), Constant.IMAGE_URI, it.imguri)
            Constant.setString(requireContext(), Constant.STORENAME, it.storename)
            Constant.setString(requireContext(), Constant.PHONENUMBER, it.phonenumber)
            Constant.setString(requireContext(), Constant.PASSWORD, it.password)
        }
    }

    private fun getAndDisplayProductFromDatabase(){
        firebase_manager.getProductData(
            Constant.VENDOR+"/"+"vendor_"+user_name+"_"+user_password+"/"+ Constant.PRODUCT)
        {datashot ->
            product_array_list.clear()
            for ((count, data) in datashot.withIndex()){
                val product = ProductDataClass(
                    data.child("imguri").value.toString(),
                    data.child("product").value.toString(),
                    data.child("price").value.toString(),
                    data.child("detail").value.toString())
                product_array_list.add(product)
                product_recycler_adapter.notifyItemChanged(count)
            }
        }
    }

    private fun floatingActionButtonClicked() {
        id_fab_add_product.setOnClickListener{
            Navigation.findNavController(screen_view).navigate(R.id.profileFragment_to_profileProductFragment)
        }
    }

    private fun shopProfileClicked() {
        db_reference  = FirebaseDatabase.getInstance().reference
        id_rl_shop_profile.setOnClickListener { v ->
            Navigation.findNavController(screen_view).navigate(R.id.profileFragment_to_profileEditFragment)
            Toast.makeText(context, "Profile clicked ${Constant.getString(requireContext(), Constant.USERNAME)}", Toast.LENGTH_SHORT).show()
        }
    }
}