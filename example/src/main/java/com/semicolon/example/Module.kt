package com.semicolon.example

import com.semicolon.cocket.CocketClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    fun provideCocketClient(): CocketClient =
        CocketClient.Builder()
            .baseUrl("http://211.38.86.92:8089")
            .build()

    @Provides
    fun provideChatService(
        cocketClient: CocketClient
    ): ChatService =
        cocketClient.create(ChatService::class.java)
}