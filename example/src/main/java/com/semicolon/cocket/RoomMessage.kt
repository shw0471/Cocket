package com.semicolon.cocket

import com.google.gson.annotations.SerializedName

data class RoomMessage(
    @SerializedName("content") val content: String,
    @SerializedName("roomId") val roomId: String
)