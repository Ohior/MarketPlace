package com.example.marketplace.shop

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.adapter.ProductRecyclerAdapter
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.ProductDataClass
import com.example.marketplace.tool.VendorDataClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ShopFragment : Fragment() {
    private lateinit var id_rl_shop_profile: RelativeLayout
    private lateinit var id_fab_add_product: FloatingActionButton
    private lateinit var id_rv_shop_items: RecyclerView
    private lateinit var id_image_profile: ImageView
    private lateinit var id_tv_profile_shop_name: TextView
    private lateinit var id_tv_profile_address: TextView
    private lateinit var id_tv_profile_name: TextView

    private lateinit var db_reference: DatabaseReference
    private lateinit var user_name: String
    private lateinit var user_password: String
    private lateinit var image_uri: String
    private lateinit var firebase_manager: FirebaseManager

    private lateinit var product_recycler_adapter: ProductRecyclerAdapter
    private lateinit var product_array_list: ArrayList<ProductDataClass>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_name = Constant.getString(requireContext(), Constant.USERNAME).toString()
        user_password = Constant.getString(requireContext(), Constant.PASSWORD).toString()
        image_uri = Constant.getString(requireContext(), Constant.IMAGEURI).toString()

        product_array_list = ArrayList()

        db_reference = FirebaseDatabase.getInstance().getReference()
        firebase_manager = FirebaseManager()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        //initialize the views
        id_rl_shop_profile = view.findViewById(R.id.id_rl_shop_profile)
        id_fab_add_product = view.findViewById(R.id.id_fab_add_product)
        id_rv_shop_items = view.findViewById(R.id.id_rv_shop_items)
        id_image_profile = view.findViewById(R.id.id_image_profile)
        id_tv_profile_shop_name = view.findViewById(R.id.id_tv_profile_shop_name)
        id_tv_profile_address = view.findViewById(R.id.id_tv_profile_address)
        id_tv_profile_name = view.findViewById(R.id.id_tv_profile_name)

        //create layout for product recycler view
        id_rv_shop_items.layoutManager = GridLayoutManager(context, Constant.COL_COUNT)

        // set the ProductRecyclerAdapter
        product_recycler_adapter = ProductRecyclerAdapter(requireContext(),product_array_list)
        id_rv_shop_items.adapter = product_recycler_adapter

        product_recycler_adapter.setOnItemClickListener(
            object : ProductRecyclerAdapter.onItemClickListener {
                override fun onItemClick(position: Int, view: View) {
                    Constant.showLongToast(requireContext(), "$position was clicked")
                }

            }
        )

        firebase_manager.getVendorData(
            Constant.VENDOR+"/"+user_name+"_"+user_password+"/"+user_name) {
//            id_image_profile.setImageURI(Uri.parse(it.imguri))
            Picasso.get().load(it.imguri).into(id_image_profile)
            id_tv_profile_name.text = it.username
//            id_tv_profile_shop_name.text = it.username
            id_tv_profile_shop_name.text = it.storename
            id_tv_profile_address.text = it.address
            Constant.setString(requireContext(), Constant.USERNAME, it.username)
            Constant.setString(requireContext(), Constant.ADDRESS, it.address)
            Constant.setString(requireContext(), Constant.IMAGEURI, it.imguri)
            Constant.setString(requireContext(), Constant.STORENAME, it.storename)
            Constant.setString(requireContext(), Constant.PHONENUMBER, it.phonenumber)
            Constant.setString(requireContext(), Constant.PASSWORD, it.password)
        }

//        shopProfileClicked(view)
//        getDataFromDatabase(user_name)
        getAndDisplayProductFromDatabase()
        floatingActionButtonClicked(view)
        return view
    }

    private fun getAndDisplayProductFromDatabase() {

        db_reference.child("vendor").child(user_name+"_"+user_password).child(Constant.PRODUCT)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear product to prevent duplicating product
                    product_array_list.clear()
                    //use snapshot to get data from firebase database
                    for (sshot in snapshot.children){
                        val product = ProductDataClass(
                            sshot.child("imguri").value.toString(),
                            sshot.child("product").value.toString(),
                            sshot.child("price").value.toString(),
                            sshot.child("detail").value.toString())

                        product_array_list.add(product)
                    }
                    product_recycler_adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Constant.showLongToast(requireContext(), "An error occur")
                }

            })
    }

    private fun floatingActionButtonClicked(view: View) {
        id_fab_add_product.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.shopFragment_to_addProductFragment)
        }
    }
//
//    private fun shopProfileClicked(view: View) {
//        db_reference  = FirebaseDatabase.getInstance().getReference()
//        id_rl_shop_profile.setOnClickListener { v ->
//            Toast.makeText(context, "Profile clicked ${Constant.getString(requireContext(), Constant.USERNAME)}", Toast.LENGTH_SHORT).show()
//        }
//    }
}