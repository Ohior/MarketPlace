package com.example.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.ProductDataClass
import com.squareup.picasso.Picasso

class ProductRecyclerAdapter(
    private val context: Context,
    private val recycler_view: RecyclerView,
    column_count:Int
): RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder>() {
    private val array_list: ArrayList<ProductDataClass> = ArrayList()
    private lateinit var click_listener: OnClickListener

    init {
        this.recycler_view.layoutManager = CustomGridLayoutManager(context, column_count)
        this.recycler_view.adapter = this
    }

    interface OnClickListener{
        fun onItemClick(position: Int, view: View)
        fun onLongItemClick(position: Int, view:View)

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
        val productitem = array_list[position]

        Picasso.get().load(array_list[position].imguri).into(holder.imguri)
        holder.itemprice.text = productitem.price
        holder.itemdetali.text = productitem.detail
        holder.itemproduct.text = productitem.product
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

    fun addToAdapter(element: ProductDataClass){
        // add item to your recyclerview
        array_list.add(element)
    }

    fun getItem(index: Int): ProductDataClass {
        return array_list[index]
    }

    fun addToAdapter(index:Int, element: ProductDataClass){
        // add item to an index spot of your recyclerview
        array_list.add(index, element)
    }

    class ProductViewHolder(item_view: View, listener: OnClickListener): RecyclerView.ViewHolder(item_view){
        val imguri: ImageView = item_view.findViewById(R.id.id_iv_product_img)
        val itemprice: TextView = item_view.findViewById(R.id.id_store_item_price)
        val itemproduct: TextView = item_view.findViewById(R.id.id_store_item_pname)
        val itemdetali: TextView = item_view.findViewById(R.id.id_store_item_detail)

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
}
