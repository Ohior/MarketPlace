package com.example.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.dataclass.ProductDataClass

class ShopRecyclerAdapter(
    private val context: Context,
    private val product_list: ArrayList<ProductDataClass>):
    RecyclerView.Adapter<ShopRecyclerAdapter.ShopViewHolder>() {

    private lateinit var click_listener: OnItemClickListener


    interface OnItemClickListener{
        fun onItemClick(position: Int, view: View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        click_listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shop_items, parent, false)
        return ShopViewHolder(view, click_listener)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        //bind data to viewholder
        val productitem = product_list[position]

        holder.itemprice.text = productitem.price
        holder.itemdetali.text = productitem.detail
        holder.itemproduct.text = productitem.product
    }

    override fun getItemCount(): Int {
        return product_list.size
    }
    class ShopViewHolder(item_view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(item_view){
        val itemprice: TextView = item_view.findViewById(R.id.id_store_item_price)
        val itemproduct: TextView = item_view.findViewById(R.id.id_store_item_pname)
        val itemdetali: TextView = item_view.findViewById(R.id.id_store_item_detail)

        init {
            item_view.setOnClickListener{
                listener.onItemClick(adapterPosition, item_view)
            }
        }
    }
}
