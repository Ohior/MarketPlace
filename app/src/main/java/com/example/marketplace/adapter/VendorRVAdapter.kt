package com.example.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.VendorDataClass
import com.squareup.picasso.Picasso

class VendorRVAdapter(
    private var context: Context,
    private var recycler_view: RecyclerView,
    column_count:Int
) : RecyclerView.Adapter<VendorRVAdapter.VendorRVViewHolder>() {
    private var array_list: ArrayList<VendorDataClass> = ArrayList()

    private lateinit var click_listener:OnItemClickListener

    init {
        this.recycler_view.layoutManager = GridLayoutManager(context, column_count)
        this.recycler_view.adapter = this
    }

    interface OnItemClickListener{
        // inter face for auto loading itemClick and longItemClick
        fun onItemClick(position: Int, view: View)
        fun onLongItemClick(position: Int, view: View)
    }

    fun onClickListener(listener: OnItemClickListener){
        //this function handle click and long click
        //do not use this function if you are not sure what to do
        //use this function like this in your fragment or activity file
        //*********************************************************
//        your_recycler_view_adapter.onClickListener(
//            object : RecyclerAdapter.OnItemClickListener {
//                override fun onItemClick(position: Int, view: View) {
//                    TODO("Your click execution should be here")
//                }
//
//                override fun onLongItemClick(position: Int, view: View) {
//                    TODO("Your long click execution should be here")
//                }
//
//            }
//        )
        //*********************************************************************

        click_listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorRVViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.vendor_items, parent, false)
        return VendorRVViewHolder(view, click_listener)
    }

    override fun onBindViewHolder(holder: VendorRVViewHolder, position: Int) {
        //bind data to viewholder
        val vendoritem = array_list[position]
        try {
            Picasso.get().load(vendoritem.imguri).into(holder.img)
        }catch (e:IllegalArgumentException){}
        holder.shopname.text = vendoritem.storename
        holder.address.text = vendoritem.address
        if (holder.address.text.isEmpty()){
            holder.address.text = "No address yet"
        }
    }

    override fun getItemCount(): Int {
        return array_list.size
    }


    fun clearAdapter(){
        //remove all item from your recyclerview
        array_list.clear()
    }

    fun removeItem(index: Int){
        array_list.removeAt(index)
    }

    fun addToAdapter(element: VendorDataClass){
        // add item to your recyclerview
        array_list.add(element)
    }

    fun getItem(index: Int): VendorDataClass{
        return array_list[index]
    }

    fun addToAdapter(index:Int, element: VendorDataClass){
        // add item to an index spot of your recyclerview
        array_list.add(index, element)
    }

    class VendorRVViewHolder(item_view: View, listener:OnItemClickListener): RecyclerView.ViewHolder(item_view) {
        val img: ImageView = item_view.findViewById(R.id.id_image_profile)
        val shopname: TextView = item_view.findViewById(R.id.id_tv_profile_shop_name)
        val address: TextView = item_view.findViewById(R.id.id_tv_profile_address)
        init {
            item_view.setOnLongClickListener{
                listener.onLongItemClick(adapterPosition, item_view)
                true
            }
            item_view.setOnClickListener {
                listener.onItemClick(adapterPosition, item_view)
            }
        }
    }
//
//    init {
//        this.recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//    }
}