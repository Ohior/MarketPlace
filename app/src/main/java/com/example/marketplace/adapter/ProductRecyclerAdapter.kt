package com.example.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.tool.ProductDataClass
import com.squareup.picasso.Picasso

class ProductRecyclerAdapter(
    private val context: Context,
    private val product_list: ArrayList<ProductDataClass>):
    RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder>() {

    private lateinit var click_listener: OnClickListener


    interface OnClickListener{
        fun onItemClick(position: Int, view: View)
    }

    fun setOnItemClickListener(listener: OnClickListener){
        click_listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_items, parent, false)
        return ProductViewHolder(view, click_listener)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        //bind data to viewholder
        val productitem = product_list[position]

        Picasso.get().load(product_list[position].imguri).into(holder.imguri)
        holder.itemprice.text = productitem.price
        holder.itemdetali.text = productitem.detail
        holder.itemproduct.text = productitem.product
    }

    override fun getItemCount(): Int {
        return product_list.size
    }
    class ProductViewHolder(item_view: View, listener: OnClickListener): RecyclerView.ViewHolder(item_view){
        val imguri: ImageView = item_view.findViewById(R.id.id_iv_product_img)
        val itemprice: TextView = item_view.findViewById(R.id.id_store_item_price)
        val itemproduct: TextView = item_view.findViewById(R.id.id_store_item_pname)
        val itemdetali: TextView = item_view.findViewById(R.id.id_store_item_detail)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition, item_view)
            }
        }
    }
}
