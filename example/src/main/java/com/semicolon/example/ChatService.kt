package com.semicolon.example

import com.semicolon.example.annotation.Connect
import com.semicolon.example.annotation.Receive
import com.semicolon.example.annotation.Send
import kotlinx.coroutines.flow.Flow

interface ChatService {

    @Connect
    suspend fun connectChatService()

    @Send("send.broadcast")
    suspend fun sendBroadCastMessage(broadcastMessage: BroadcastMessage)

    @Send("join")
    suspend fun joinChatRoom(roomInfo: RoomInfo)

    @Send("send.room")
    suspend fun sendRoomMessage(roomMessage: RoomMessage)

    @Receive("message")
    fun receiveBroadcast(): Flow<BroadcastMessage>

    @Receive("roomMessage")
    fun receiveRoomMessage(): Flow<RoomMessage>
}