package com.semicolon.example

import com.semicolon.cocket.annotation.*
import kotlinx.coroutines.flow.Flow

interface ChatService {

    @Connect
    suspend fun connectChatService()

    @Disconnect
    suspend fun disconnectChatService()

    @Emit("send.broadcast")
    suspend fun sendBroadCastMessage(broadcastMessage: BroadcastMessage)

    @Emit("join")
    suspend fun joinChatRoom(roomInfo: RoomInfo)

    @Emit("send.room")
    suspend fun sendRoomMessage(roomMessage: RoomMessage)

    @On("message")
    fun receiveBroadcast(): Flow<BroadcastMessage>

    @On("roomMessage")
    fun receiveRoomMessage(): Flow<RoomMessage>

    @Off
    fun offAllEvents()
}