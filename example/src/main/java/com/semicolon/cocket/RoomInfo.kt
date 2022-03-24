package com.semicolon.cocket

import com.google.gson.annotations.SerializedName

data class RoomInfo(
    @SerializedName("roomId") val roomId: String
)
