package com.example.marketplace.shop

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.example.marketplace.adapter.ProductRecyclerAdapter
import com.example.marketplace.market.MarketActivity
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.data.ProductDataClass
import com.example.marketplace.data.UserDatabase
import com.example.marketplace.data.VendorDataClass
import com.example.marketplace.tool.Tool
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
    private var user_name: String? = null
    private var user_type: String? = null
    private var user_password: String? = null

    //adapter
    private lateinit var product_recycler_adapter: ProductRecyclerAdapter

    //clicked database
    private lateinit var vendor_database: UserDatabase

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.shop_menu, menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
        return when (item.itemId){
            R.id.id_menu_chat -> {
                when (user_type) {
                    "null" -> {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        Toast.makeText(requireContext(), "You must be Login or be a customer to chat", Toast.LENGTH_SHORT).show()
                        requireActivity().finish()
                    }
                    "customer" -> {
                        Navigation.findNavController(screen_view).navigate(R.id.shopFragment_to_chatFragment)
                    }
                    else->Tool.showShortToast(requireContext(), "You must be Login or be a customer to chat")
                }
//                Toast.makeText(requireContext(), "$user_name Chat", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.id_menu_market ->{
                requireActivity().finish()
                startActivity(Intent(requireContext(), MarketActivity::class.java))
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

        //set firebase for functionality
        db_reference = FirebaseDatabase.getInstance().getReference()
        firebase_manager = FirebaseManager(requireContext())
        vendor_database = UserDatabase(requireContext(), Constant.VENDOR_DB_NAME)
        vendor_database.deleteDatabaseTable()
        setHasOptionsMenu(true)
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
        // set the ProductRecyclerAdapter
        product_recycler_adapter = ProductRecyclerAdapter(requireContext(), id_rv_shop_items, Constant.COL_COUNT)
//        id_rv_shop_items.adapter = product_recycler_adapter

        val password = Constant.getString(requireContext(), Constant.CLICK_PASSWORD)
        val username = Constant.getString(requireContext(), Constant.CLICK_USER)
        Tool.debugMessage("password is $password and user is $username")
        firebase_manager.getVendorData(
            Constant.VENDOR+"/vendor_"+username+"_"+password+"/user"){
            try {
                Picasso.get().load(it.imguri).into(id_image_profile)
            }catch(e:IllegalArgumentException){}
            id_tv_profile_name.text = it.username
            id_tv_profile_shop_name.text = it.storename
            id_tv_profile_address.text = it.address
            if (it.address.isEmpty()){
                id_tv_profile_address.text = "No address yet"
            }
            Tool.debugMessage("lat ${it.latitude} long ${it.longitude}")
            Tool.debugMessage(it.toString())
            vendor_database.insertIntoDatabase(VendorDataClass(
                imguri = it.imguri,
                username = it.username,
                password = it.password,
                phonenumber = it.phonenumber,
                storename = it.storename,
                address = it.address,
                latitude = it.latitude,
                longitude = it.longitude
            ))
        }

        // LOAD
        firebase_manager.getProductData(
            Constant.VENDOR+"/vendor_"+username+"_"+password+"/"+Constant.PRODUCT){snapshot ->
            product_recycler_adapter.clearAdapter()
            for ((count, shot) in snapshot.withIndex()){
                val product = ProductDataClass(
                    shot.child("imguri").value.toString(),
                    shot.child("product").value.toString(),
                    shot.child("price").value.toString(),
                    shot.child("detail").value.toString())
                product_recycler_adapter.addToAdapter(product)
                product_recycler_adapter.notifyItemChanged(count)
            }
        }

        //  CHECKING FOR PRODUCT CLICKED
        product_recycler_adapter.setOnItemClickListener(
            object : ProductRecyclerAdapter.OnClickListener {
                override fun onItemClick(position: Int, view: View) {
                    val itemimguri: ImageView = view.findViewById(R.id.id_iv_product_img)
                    val itemprice: TextView = view.findViewById(R.id.id_store_item_price)
                    val itemproduct: TextView = view.findViewById(R.id.id_store_item_pname)
                    val itemdetali: TextView = view.findViewById(R.id.id_store_item_detail)
                    Constant.showShortToast(requireContext(), itemproduct.text.toString())
                }

                override fun onLongItemClick(position: Int, view: View) {
                    val itemimguri: ImageView = view.findViewById(R.id.id_iv_product_img)
                    val itemprice: TextView = view.findViewById(R.id.id_store_item_price)
                    val itemproduct: TextView = view.findViewById(R.id.id_store_item_pname)
                    val itemdetali: TextView = view.findViewById(R.id.id_store_item_detail)
                    Constant.showShortToast(requireContext(), itemprice.text.toString())                }

            }
        )

        id_fab_add_product.visibility = FloatingActionButton.GONE
        Tool.debugMessage("ShopFragmentClass")


        //configure this fragment for menu item
        return screen_view.rootView
    }
}