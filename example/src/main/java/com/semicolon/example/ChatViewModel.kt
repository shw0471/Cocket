package com.semicolon.example

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
        chatService.connectChatService(
            "Bearer testtokensdwfwqrqefwdefwqedwwdf"
        )
        chatService.receiveBroadcast().collect {
            broadcastMessageLiveData.value = it.content
        }
    }

    fun disconnectChatService() = viewModelScope.launch {
        chatService.offAllEvents()
        chatService.disconnectChatService()
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
            chatService.sendBroadCastMessage(BroadcastMessage(message))
        }

    fun sendRoomMessage(message: String) =
        viewModelScope.launch { chatService.sendRoomMessage(RoomMessage(message, currentRoomId)) }
}