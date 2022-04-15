package com.example.marketplace.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.MessageDataClass

class MessageAdapter(
    private val context: Context,
    private val message_list: ArrayList<MessageDataClass>,
    val user_name: String
):
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private lateinit var click_listener: OnClickListener


    interface OnClickListener {
        fun onItemClick(position: Int, view: View)
        fun onLongItemClick(position: Int, view: View)

    }

    fun setOnItemClickListener(listener: OnClickListener) {
        click_listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_items, parent, false)
        return MessageViewHolder(view, click_listener)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        //bind data to viewholder
        val messageitem = message_list[position]
        holder.name.text = messageitem.name
        holder.message.text = messageitem.message
        if(user_name.lowercase() == messageitem.message.lowercase()){
            holder.name.textSize = 20f
            holder.message.textSize = 15f
            holder.name.setTypeface(holder.name.typeface, Typeface.BOLD_ITALIC)
            holder.llayout.setBackgroundColor(Color.parseColor("#cae00d"))
        }
        if(user_name.lowercase() == messageitem.name.lowercase()){
            holder.message.textSize = 20f
            holder.message.setTypeface(holder.name.typeface, Typeface.BOLD_ITALIC)
            holder.name.textSize = 15f
            holder.llayout.setBackgroundColor(Color.parseColor("#cae00d"))
        }
    }

    override fun getItemCount(): Int {
        return message_list.size
    }

    class MessageViewHolder(item_view: View, listener: OnClickListener) :
        RecyclerView.ViewHolder(item_view) {
        val name: TextView = item_view.findViewById(R.id.id_tv_profile_name)
        val message: TextView = item_view.findViewById(R.id.id_tv_message)
        val llayout: LinearLayout = item_view.findViewById(R.id.id_ll_message_item)

        init {
            item_view.setOnLongClickListener {
                listener.onLongItemClick(adapterPosition, item_view)
                true
            }
            item_view.setOnClickListener {
                listener.onItemClick(adapterPosition, item_view)
            }
        }
    }
}