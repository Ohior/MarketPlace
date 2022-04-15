package com.example.marketplace.market

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.adapter.VendorRecyclerAdapter
import com.example.marketplace.data.VendorDataClass
import com.example.marketplace.shop.ShopActivity
import com.example.marketplace.tool.*
import com.google.firebase.auth.FirebaseAuth

class MarketFragment : Fragment() {
    private lateinit var screen_view: View
    //ids
    private lateinit var id_market_rv: RecyclerView

    //database
    private lateinit var firebase_manager: FirebaseManager
    private lateinit var firebase_auth: FirebaseAuth

    //adapter
    private lateinit var vendor_recycler_adapter: VendorRecyclerAdapter

    //array list
    private lateinit var vendor_array_list: ArrayList<VendorDataClass>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initialize firebase manager
        firebase_manager = FirebaseManager(requireContext())
        firebase_auth = FirebaseAuth.getInstance()


        //initialize array list
        vendor_array_list = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view =  inflater.inflate(R.layout.fragment_market, container, false)
        //initialize views
        id_market_rv = screen_view.findViewById(R.id.id_market_rv)

        //create layout for Recycler view
        id_market_rv.layoutManager = GridLayoutManager(context, 2)

        //initialize RV adapter
        vendor_recycler_adapter = VendorRecyclerAdapter(requireContext(), vendor_array_list)
        id_market_rv.adapter = vendor_recycler_adapter

        // Market
        displayMarketStore()

        // check if store/ vendor was clicked
        vendorClickListener()

        return screen_view.rootView
    }

    private fun vendorClickListener() {
        vendor_recycler_adapter.setOnItemClickListener(
            object : VendorRecyclerAdapter.OnClickListener {
                override fun onItemClick(position: Int, view: View) {
                    val vendordata =  vendor_array_list[position]
                    Constant.setString(requireContext(), Constant.CLICK_USER, vendordata.username)
                    Constant.setString(requireContext(), Constant.CLICK_PASSWORD, vendordata.password)
                    val intent = Intent(requireContext(), ShopActivity::class.java)
                    startActivity(intent)
                }

                override fun onLongItemClick(position: Int, view: View) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    private fun displayMarketStore() {
        Tool.loadingProgressBar(requireContext(), "getting shops..."){probar->
        firebase_manager.getFirebaseDatas("vendor"){snapshot ->
            vendor_array_list.clear()
            for ((count, shot) in snapshot.withIndex()){
                val split = shot.key.toString().split("_")
                firebase_manager.getVendorDataBool("vendor" +
                        "/" + split[0] +
                        "_" + split[1] +
                        "_" + split[2] +
                        "/" + split[1]
                ) {data, bool ->
                    vendor_array_list.add(data)
//                    vendor_recycler_adapter.notifyDataSetChanged()
                    vendor_recycler_adapter.notifyItemChanged(count)
                    if (bool){
                        probar.dismiss()
                    }
                }
            }
        }
        }
    }
}