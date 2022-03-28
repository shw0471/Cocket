package com.semicolon.example

import com.semicolon.example.annotation.Connect
import com.semicolon.example.annotation.Receive
import com.semicolon.example.annotation.Emit
import kotlinx.coroutines.flow.Flow

interface ChatService {

    @Connect
    suspend fun connectChatService()

    @Emit("send.broadcast")
    suspend fun sendBroadCastMessage(broadcastMessage: BroadcastMessage)

    @Emit("join")
    suspend fun joinChatRoom(roomInfo: RoomInfo)

    @Emit("send.room")
    suspend fun sendRoomMessage(roomMessage: RoomMessage)

    @Receive("message")
    fun receiveBroadcast(): Flow<BroadcastMessage>

    @Receive("roomMessage")
    fun receiveRoomMessage(): Flow<RoomMessage>
}