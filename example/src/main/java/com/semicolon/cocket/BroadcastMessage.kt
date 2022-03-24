package com.semicolon.cocket

import com.google.gson.annotations.SerializedName

data class BroadcastMessage(
    @SerializedName("content") val content: String
)