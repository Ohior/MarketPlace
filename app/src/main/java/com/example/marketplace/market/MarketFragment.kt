package com.example.marketplace.market

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.adapter.VendorRecyclerAdapter
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.tool.VendorDataClass
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

        vendorClickListener()

        // Market
        displayMarketStore()


        return screen_view.rootView
    }

    private fun vendorClickListener() {
        vendor_recycler_adapter.setOnItemClickListener(
            object : VendorRecyclerAdapter.OnClickListener {
                override fun onItemClick(position: Int, view: View) {
                    Constant.showShortToast(requireContext(), "$position was clicked")
                }

            }
        )
    }

    private fun displayMarketStore() {
        var count = 0
        firebase_manager.getFirebaseDatas("vendor"){snapshots ->
            vendor_array_list.clear()
            for (snapshot in snapshots){
                count ++
                val split = snapshot.key.toString().split("_")
                firebase_manager.getVendorData(
                    "vendor/"
                            + split[0] + "_" + split[1] + "/"
                            + split[0]) {
                    vendor_array_list.add(it)
//                    vendor_recycler_adapter.notifyDataSetChanged()
                    vendor_recycler_adapter.notifyItemChanged(count)
                }
                Constant.debugMessage(snapshot.key.toString()+"and"+split,tag="KEY")
            }
        }
    }
}