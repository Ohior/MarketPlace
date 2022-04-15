package com.example.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.ChatListDataClass

class ChatListAdapter(
    private var context: Context,
    private var recycler_view: RecyclerView,
    private var layout: Int,) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
    // replace all the DataClass in this file with your own data class name
    private var array_list: ArrayList<ChatListDataClass> = ArrayList()
//    private var recycler_view: RecyclerView = recyclerview

    init {
        this.recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        this.recycler_view.adapter = this
    }

    private lateinit var click_listener:OnItemClickListener

    interface OnItemClickListener{
        // inter face for auto loading itemClick and longItemClick
        fun onItemClick(position: Int, view: View)
        fun onLongItemClick(position: Int, view: View)
    }

    fun onClickListener(listener:OnItemClickListener){
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view, click_listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatlist = array_list[position]
        holder.name.text = chatlist.name
        holder.name.textSize = 20f
        holder.message.text = chatlist.message
        holder.hidden.text = chatlist.password
    }

    override fun getItemCount(): Int {
        //get the number of item in your recyclerview
        return array_list.size
    }

    fun clearAdapter(){
        //remove all item from your recyclerview
        array_list.clear()
    }

    fun addToAdapter(element: ChatListDataClass){
        // add item to your recyclerview
        array_list.add(element)
    }

    fun addToAdapter(index:Int, element: ChatListDataClass){
        // add item to an index spot of your recyclerview
        array_list.add(index, element)
    }


    class ViewHolder(item_view: View, listener:OnItemClickListener): RecyclerView.ViewHolder(item_view) {
        val name: TextView = item_view.findViewById(R.id.id_tv_profile_name)
        val message: TextView = item_view.findViewById(R.id.id_tv_message)
        val hidden: TextView = item_view.findViewById(R.id.id_tv_hidden)
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

    init {
        this.recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }
}
