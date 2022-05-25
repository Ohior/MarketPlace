package com.example.marketplace.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.adapter.CustomGridLayoutManager
import com.example.marketplace.adapter.MessageAdapter
import com.example.marketplace.data.MessageDataClass
import com.example.marketplace.tool.Constant
import com.google.firebase.database.*


class ChatFragment : Fragment() {
    private lateinit var screen_view: View
    private lateinit var id_rv_chat_message: RecyclerView
    private lateinit var id_et_chat: EditText
    private lateinit var id_bt_send_chat: ImageView

    private lateinit var click_user: String
    private lateinit var click_password: String
    private lateinit var user_name: String
    private lateinit var user_password: String
    private lateinit var user_type: String

    private var reciever_room: String? = null
    private var sender_room: String? = null

    private lateinit var message_adapter: MessageAdapter

    private lateinit var  firebase_refrence: DatabaseReference

    private lateinit var array_list: ArrayList<MessageDataClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase_refrence = FirebaseDatabase.getInstance().reference
        user_name = Constant.getString(requireContext(), Constant.USERNAME).toString()
        user_type = Constant.getString(requireContext(), Constant.USER_TYPE).toString()
        user_password = Constant.getString(requireContext(), Constant.PASSWORD).toString()

        click_user = Constant.getString(requireContext(), Constant.CLICK_USER).toString()
        click_password = Constant.getString(requireContext(), Constant.CLICK_PASSWORD).toString()
        array_list = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view = inflater.inflate(R.layout.fragment_chat, container, false)
        id_rv_chat_message = screen_view.findViewById(R.id.id_rv_chat_message)
        id_et_chat = screen_view.findViewById(R.id.id_et_chat)
        id_bt_send_chat = screen_view.findViewById(R.id.id_bt_send_chat)
        message_adapter = MessageAdapter(requireContext(), array_list, user_name)
//        id_rv_chat_message.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        id_rv_chat_message.layoutManager = CustomGridLayoutManager(requireContext())
        id_rv_chat_message.adapter = message_adapter

        reciever_room = "chat_$click_user"
        sender_room = "chat_$user_name"

        addDataToRecyclerView()

        sendButtonClicked()

        recyclerViewClickListener()

        this.setMenuVisibility(false)

        return screen_view.rootView
    }

    private fun recyclerViewClickListener() {
        message_adapter.setOnItemClickListener(
            object: MessageAdapter.OnClickListener{
                override fun onItemClick(position: Int, view: View) {
                }

                override fun onLongItemClick(position: Int, view: View) {
                }

            }
        )
    }

    private fun addDataToRecyclerView() {
        firebase_refrence
            .child(user_type+"/"+user_type+"_$user_name"+"_$user_password"+"/chat")
            .child("chat_$click_user"+"_"+click_password)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    array_list.clear()
                    for ((index, data) in snapshot.children.withIndex()) {
                        val message = data.getValue(MessageDataClass::class.java)
                        array_list.add(message!!)
                        message_adapter.notifyItemInserted(index)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun sendButtonClicked() {
        var ut = "vendor"
        id_bt_send_chat.setOnClickListener{
            if(id_et_chat.text.trim().isNotEmpty()){
                if(user_type == ut){
                    ut = "customer"
                }
                val data = id_et_chat.text.toString().trim()
                val messageobject = MessageDataClass(user_name, data)
                firebase_refrence
                    .child(user_type+"/"+user_type+"_$user_name"+"_$user_password"+"/chat")
                    .child("chat_$click_user"+"_"+click_password)
                    .push()
                    .setValue(messageobject).addOnSuccessListener {
                        firebase_refrence
                            .child(ut+"/"+ut+"_$click_user"+"_$click_password"+"/chat")
                            .child("chat_$user_name"+"_"+user_password)
                            .push()
                            .setValue(messageobject)
                    }
            }
        }
    }
}