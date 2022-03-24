package com.semicolon.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.semicolon.example.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()
    private val chatListAdapter = ChatListAdapter()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel.joinChatService()
        binding.apply {
            rvChat.adapter = chatListAdapter
            btnSendBroadcast.setOnClickListener {
                val broadcastMessage = binding.etSendBroadcast.text.toString()
                if (broadcastMessage.isNotEmpty()) {
                    binding.etSendBroadcast.setText("")
                    viewModel.sendBroadcastMessage(broadcastMessage)
                }
            }
            btnJoinRoom.setOnClickListener {
                val roomId = binding.etJoinRoom.text.toString()
                if (roomId.isNotEmpty()) {
                    binding.etJoinRoom.setText("")
                    viewModel.joinChatRoom(roomId)
                }
            }
            btnSendMessage.setOnClickListener {
                val roomMessage = binding.etSendMassage.text.toString()
                if (roomMessage.isNotEmpty()) {
                    binding.etSendMassage.setText("")
                    viewModel.sendRoomMessage(roomMessage)
                }
            }
        }
        observe()
    }

    private fun observe() {
        val activity = this
        viewModel.apply {
            broadcastMessageLiveData.observe(activity) {
                Toast.makeText(activity, it, LENGTH_SHORT).show()
            }
            roomMessageLiveData.observe(activity) {
                chatListAdapter.addChat(it)
            }
        }
    }
}