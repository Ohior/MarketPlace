package com.example.marketplace.adapter

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.tool.VendorDataClass
import com.squareup.picasso.Picasso

class VendorRecyclerAdapter(
    private val context: Context,
    private val vendor_list: ArrayList<VendorDataClass>):
    RecyclerView.Adapter<VendorRecyclerAdapter.VendorViewHolder>() {

    private lateinit var click_listener: OnClickListener


    interface OnClickListener{
        fun onItemClick(position: Int, view: View)
    }

    fun setOnItemClickListener(listener: OnClickListener){
        click_listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.vendor_items, parent, false)
        return VendorViewHolder(view, click_listener)
    }

    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        //bind data to viewholder
        val vendoritem = vendor_list[position]

        Picasso.get().load(vendor_list[position].imguri).into(holder.img)
        holder.shopname.text = vendoritem.storename
        holder.address.text = vendoritem.address
        holder.shopname.movementMethod = ScrollingMovementMethod()
        holder.address.movementMethod = ScrollingMovementMethod()
    }

    override fun getItemCount(): Int {
        return vendor_list.size
    }
    class VendorViewHolder(item_view: View, listener: OnClickListener): RecyclerView.ViewHolder(item_view){
        val img: ImageView = item_view.findViewById(R.id.id_image_profile)
        val shopname: TextView = item_view.findViewById(R.id.id_tv_profile_shop_name)
        val address: TextView = item_view.findViewById(R.id.id_tv_profile_address)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition, item_view)
            }
        }
    }
}
