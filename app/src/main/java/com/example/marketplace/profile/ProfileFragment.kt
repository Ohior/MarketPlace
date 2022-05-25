package com.example.marketplace.profile


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
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.example.marketplace.adapter.ProductRecyclerAdapter
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.data.ProductDataClass
import com.example.marketplace.data.ProductDatabase
import com.example.marketplace.data.UserDatabase
import com.example.marketplace.market.MarketActivity
import com.example.marketplace.tool.Tool
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var firebase_manager: FirebaseManager

    //
    private lateinit var user_name: String
    private lateinit var user_type: String
    private lateinit var user_password: String

    //adapter
    private lateinit var product_recycler_adapter: ProductRecyclerAdapter

    //DATABASE
    private lateinit var user_database: UserDatabase
    private lateinit var product_database: ProductDatabase


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
                true
            }
            R.id.id_menu_location ->{
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
        product_database = ProductDatabase(requireContext(), Constant.PRODUCT_DB_NAME)
        if(user_type == "customer"){
            Navigation.findNavController(screen_view).navigate(R.id.profileFragment_to_customerProfileEditFragment)
        }
        //set firebase for functionality
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
        // set the ProductRecyclerAdapter
        product_recycler_adapter = ProductRecyclerAdapter(requireContext(), id_rv_shop_items, Constant.COL_COUNT)

        productRecyclerListener()
        getAndStoreVendorData()
        shopProfileClicked()
        getAndDisplayProductFromDatabase()
        floatingActionButtonClicked()

        //configure this fragment for menu item
        setHasOptionsMenu(true)
        return screen_view.rootView
    }

    private fun productRecyclerListener() {
        product_recycler_adapter.setOnItemClickListener(
            object : ProductRecyclerAdapter.OnClickListener {
                override fun onItemClick(position: Int, view: View) {
                    Tool.debugMessage(product_database.getFromDataBase(position).toString(), "DATABASE")
//                    Navigation.findNavController(screen_view)
//                        .navigate(R.id.profileFragment_to_profileProductFragment)
                }

                override fun onLongItemClick(position: Int, view: View) {
                    val itemproduct: TextView = view.findViewById(R.id.id_store_item_pname)
                    onProductLongClick(itemproduct, position)
                }

            }
        )
    }

    private fun onProductLongClick(itemproduct: TextView, position: Int) {
        val productpath:String = user_type+ "/"+
                user_type+"_"+user_name+"_"+user_password+
                "/product"
        // CONFIRMING USER DELETION
        Tool.popUpMessage(requireContext(), "Delete Product '${itemproduct.text}'")
        { popup ->
            popup.setPositiveButton("delete"){ _, _ ->
                //ANIMATE DELETION
                Tool.loadingProgressBarMessage(requireContext(), "deleting product... please wait")
                {probar ->
                    // DELETE FROM LOCAL DATABASE

                    val product = product_database.deleteDatabaseById(position)
                    Tool.debugMessage(product.toString())
                    // DELETE FROM LOCAL FIREBASE DATABASE
                    firebase_manager.deleteFromDatabase(productpath, product?.product)
                    { task ->
                        task.addOnSuccessListener {
                            // DELETE FROM LOCAL FIREBASE STORAGE DATABASE
                            //TODO: add image uri to path of 'deleteFromStorageDatabase' and the right imgtype
                    if (product != null) {
                        firebase_manager.deleteFromStorageDatabase(product.imguri)
                        {task2->
                            task2.addOnSuccessListener {
                                Tool.showShortToast(requireContext(), "delete succeeded")
                                probar.dismiss()
                                product_recycler_adapter.notifyItemRemoved(position)
                            }
                            task2.addOnFailureListener {
                                Tool.showShortToast(requireContext(), "delete failed")
                                probar.dismiss()
                            }
                        }
                    }
                        }
                    }
                }
            }
            popup.setNegativeButton("back", null)
        }
    }

    private fun getAndStoreVendorData() {
        firebase_manager.getVendorData(
            Constant.VENDOR+"/"+"vendor_"+user_name+"_"+user_password+"/"+"user")
        {
            try {
                Picasso.get().load(it.imguri).into(id_image_profile)
            }catch(e:IllegalArgumentException){}
            id_tv_profile_name.text = it.username
            id_tv_profile_shop_name.text = it.storename
            id_tv_profile_address.text = it.address
            if (id_tv_profile_address.text.isEmpty()){
                id_tv_profile_address.text = "No address yet"
            }
        }
    }

    private fun getAndDisplayProductFromDatabase(){
        firebase_manager.getProductData(
            Constant.VENDOR+"/"+"vendor_"+user_name+"_"+user_password+"/"+ Constant.PRODUCT)
        {datashot ->
            product_database.deleteDatabaseTable()
            product_recycler_adapter.clearAdapter()
            for ((count, data) in datashot.withIndex()){
                val product = ProductDataClass(
                    data.child("imguri").value.toString(),
                    data.child("product").value.toString(),
                    data.child("price").value.toString(),
                    data.child("detail").value.toString())
                Tool.debugMessage(data.child("imguri").value.toString())
                product_database.insertIntoDatabase(product)
                product_recycler_adapter.addToAdapter(product)
                product_recycler_adapter.notifyItemInserted(count)
            }
        }
    }

    private fun floatingActionButtonClicked() {
        id_fab_add_product.setOnClickListener{
            Navigation.findNavController(screen_view).navigate(R.id.profileFragment_to_profileProductFragment)
        }
    }

    private fun shopProfileClicked() {
        id_rl_shop_profile.setOnClickListener { v ->
            Navigation.findNavController(screen_view).navigate(R.id.profileFragment_to_profileEditFragment)
        }
    }
}