package com.example.marketplace.shop

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.adapter.ProductRecyclerAdapter
import com.example.marketplace.market.MarketActivity
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.tool.ProductDataClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class ShopFragment : Fragment() {
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
    private lateinit var user_password: String
    private lateinit var image_uri: String

    //adapter
    private lateinit var product_recycler_adapter: ProductRecyclerAdapter

    //array list
    private lateinit var product_array_list: ArrayList<ProductDataClass>


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.shop_menu, menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
        return when (item.itemId){
            R.id.id_menu_market ->{
                startActivity(Intent(requireContext(), MarketActivity::class.java))
                Toast.makeText(requireContext(), "Market", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.id_menu_location ->{
                Navigation.findNavController(screen_view).navigate(R.id.shopFragment_to_locateFragment)
                Toast.makeText(requireContext(), "select all", Toast.LENGTH_SHORT).show()
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
        user_password = Constant.getString(requireContext(), Constant.PASSWORD).toString()
        image_uri = Constant.getString(requireContext(), Constant.IMAGEURI).toString()

        product_array_list = ArrayList()

        //set firebase for functionality
        db_reference = FirebaseDatabase.getInstance().getReference()
        firebase_manager = FirebaseManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view = inflater.inflate(R.layout.fragment_shop, container, false)

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

        floatingActionButtonClicked(screen_view)

        //configure this fragment for menu item
        setHasOptionsMenu(true)
        return screen_view.rootView
    }

    private fun productRecyclerListener() {
        product_recycler_adapter.setOnItemClickListener(
            object : ProductRecyclerAdapter.OnClickListener {
                override fun onItemClick(position: Int, view: View) {
                    Constant.showShortToast(requireContext(), "$position was clicked")
                }

            }
        )
    }

    private fun getAndStoreVendorData() {
        firebase_manager.getVendorData(Constant.VENDOR+"/"+user_name+"_"+user_password+"/"+user_name)
        {
            Picasso.get().load(it.imguri).into(id_image_profile)
            id_tv_profile_name.text = it.username
            id_tv_profile_shop_name.text = it.storename
            id_tv_profile_address.text = it.address
            Constant.setString(requireContext(), Constant.USERNAME, it.username)
            Constant.setString(requireContext(), Constant.ADDRESS, it.address)
            Constant.setString(requireContext(), Constant.IMAGEURI, it.imguri)
            Constant.setString(requireContext(), Constant.STORENAME, it.storename)
            Constant.setString(requireContext(), Constant.PHONENUMBER, it.phonenumber)
            Constant.setString(requireContext(), Constant.PASSWORD, it.password)
        }
    }

    private fun getAndDisplayProductFromDatabase(){
        //this command is under testing
        firebase_manager.getProductData(Constant.VENDOR+"/"+user_name+"_"+user_password+"/"+Constant.PRODUCT)
        {shot ->
            product_array_list.clear()
            shot.forEach{
                val product = ProductDataClass(
                    it.child("imguri").value.toString(),
                    it.child("product").value.toString(),
                    it.child("price").value.toString(),
                    it.child("detail").value.toString())
                product_array_list.add(product)
            }
            product_recycler_adapter.notifyDataSetChanged()
        }
    }

    private fun floatingActionButtonClicked(view: View) {
        id_fab_add_product.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.shopFragment_to_addProductFragment)
        }
    }

    private fun shopProfileClicked() {
        db_reference  = FirebaseDatabase.getInstance().getReference()
        id_rl_shop_profile.setOnClickListener { v ->
            Toast.makeText(context, "Profile clicked ${Constant.getString(requireContext(), Constant.USERNAME)}", Toast.LENGTH_SHORT).show()
        }
    }
}