package com.semicolon.cocket

import com.semicolon.cocket.annotation.Connect
import com.semicolon.cocket.annotation.Receive
import com.semicolon.cocket.annotation.Send
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