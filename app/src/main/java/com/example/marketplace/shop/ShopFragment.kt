package com.example.marketplace.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ShopFragment : Fragment() {
    private lateinit var id_rl_shop_profile: RelativeLayout
    private lateinit var id_fab_add_product: FloatingActionButton
    private lateinit var id_rv_shop_items: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shop, container, false)
        id_rl_shop_profile = view.findViewById(R.id.id_rl_shop_profile)
        id_fab_add_product = view.findViewById(R.id.id_fab_add_product)
        id_rv_shop_items = view.findViewById(R.id.id_rv_shop_items)

        id_rv_shop_items

        shopProfileClicked(view)
        floatingActionButtonClicked(view)
        return view
    }

    private fun floatingActionButtonClicked(view: View) {
        id_fab_add_product.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.shopFragment_to_addProductFragment)
        }
    }

    private fun shopProfileClicked(view: View) {
        id_rl_shop_profile.setOnClickListener { v ->
            Toast.makeText(context, "Profile clicked", Toast.LENGTH_SHORT).show()
        }
    }
}