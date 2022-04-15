package com.example.marketplace.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.adapter.ChatListAdapter
import com.example.marketplace.data.ChatListDataClass
import com.example.marketplace.data.CustomerDataClass
import com.example.marketplace.data.MessageDataClass
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.FirebaseManager
import com.example.marketplace.tool.Tool
import com.google.firebase.auth.FirebaseAuth


class ChatListFragment : Fragment() {
    // MAKE GLOBAL VARIABLES
    private lateinit var id_rv_chat_list: RecyclerView
    private lateinit var screen_view: View
    private lateinit var chat_list_adapter: ChatListAdapter

    private lateinit var user_name: String
    private lateinit var user_type: String
    private lateinit var user_password: String

    private lateinit var firebase_manager: FirebaseManager
    private lateinit var firebase_auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().actionBar?.hide()
        // initialise your firebase manager
        firebase_auth = FirebaseAuth.getInstance()
        firebase_manager = FirebaseManager(requireContext(), requireActivity())
        // get default data from user
        user_name = Constant.getString(requireContext(), Constant.USERNAME).toString()
        user_type = Constant.getString(requireContext(), Constant.USER_TYPE).toString()
        user_password = Constant.getString(requireContext(), Constant.PASSWORD).toString()
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        screen_view = inflater.inflate(R.layout.fragment_chat_list, container, false)
        // initialise your view and adapter
        id_rv_chat_list = screen_view.findViewById(R.id.id_rv_chat_list)
        chat_list_adapter = ChatListAdapter(requireContext(), id_rv_chat_list, R.layout.chat_list_item)

        displayChatContacts()

        chatClickListener()
        return screen_view.rootView
    }

    private fun displayChatContacts() {
        val path = "vendor/vendor_$user_name"+"_$user_password/chat"
        Tool.loadingProgressBar(requireContext(), "getting shops..."){probar->
            firebase_manager.getFirebaseDatas(path){snapshot ->
                chat_list_adapter.clearAdapter()
                for ((count, shot) in snapshot.withIndex()){
                    val split = shot.key.toString().split("_")
                            "/" + split[0] +
                            "_" + split[1] +
                            "_" + split[2] +
                            "/" + split[1]
                        chat_list_adapter.addToAdapter(ChatListDataClass(split[1],"Message from", split[2]))
//                    vendor_recycler_adapter.notifyDataSetChanged()
                        chat_list_adapter.notifyItemChanged(count)
                        if (chat_list_adapter.itemCount > 0){
                            probar.dismiss()
                        }

                }
            }
        }
    }

    private fun chatClickListener() {
        chat_list_adapter.onClickListener(
            object: ChatListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, view: View) {
                    val name = view.findViewById<TextView>(R.id.id_tv_profile_name)
                    val password = view.findViewById<TextView>(R.id.id_tv_hidden)
                    Constant.setString(requireContext(), Constant.CLICK_USER, name.text.toString().trim())
                    Constant.setString(requireContext(), Constant.CLICK_PASSWORD, password.text.toString().trim())
                    Navigation.findNavController(screen_view).navigate(R.id.chatListFragment2_to_chatFragment2)
                }

                override fun onLongItemClick(position: Int, view: View) {
                    Tool.showShortToast(requireContext(), "No dey hold me like that")
                }

            }
        )
    }
}