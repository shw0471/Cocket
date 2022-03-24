package com.semicolon.cocket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatService: ChatService
) : ViewModel() {

    private var currentRoomId: String = ""
    val broadcastMessageLiveData = MutableLiveData<String>()
    val roomMessageLiveData = MutableLiveData<String>()

    fun joinChatService() = viewModelScope.launch {
        chatService.connectChatService()
        chatService.receiveBroadcast().collect {
            broadcastMessageLiveData.value = it.content
        }
    }

    fun joinChatRoom(roomId: String) = viewModelScope.launch {
        chatService.joinChatRoom(RoomInfo(roomId))
        currentRoomId = roomId
        chatService.receiveRoomMessage().collect {
            roomMessageLiveData.value = it.content
        }
    }

    fun sendBroadcastMessage(message: String) =
        viewModelScope.launch {
            println(chatService.sendBroadCastMessage(BroadcastMessage(message)))
        }

    fun sendRoomMessage(message: String) =
        viewModelScope.launch { chatService.sendRoomMessage(RoomMessage(message, currentRoomId)) }
}