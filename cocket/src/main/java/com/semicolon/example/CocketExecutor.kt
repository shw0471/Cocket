package com.semicolon.example

import com.google.gson.Gson
import com.semicolon.example.annotation.*
import io.socket.client.Ack
import io.socket.client.Socket
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CocketExecutor(
    private val socketClient: Socket
) {
    private val gson = Gson()

    suspend fun executeSuspend(method: Method, args: Array<Any>): Any {
        require(method.annotations.size == 1) { "There should be only one annotation." }
        return when (method.annotations.first()) {
            is Connect -> connect()
            is Disconnect -> disconnect()
            is Emit -> send(method, args)
            else -> throw IllegalArgumentException("invalid annotation.")
        }
    }

    fun execute(method: Method): Any {
        require(method.annotations.size == 1) { "There should be only one annotation." }
        return when(method.annotations.first()) {
            is On -> receive(method)
            is Once -> receiveOnce(method)
            else -> throw IllegalArgumentException("invalid annotation.")
        }
    }

    private suspend fun connect() {
        socketClient.connect()
        return suspendCoroutine {
            socketClient.once(Socket.EVENT_CONNECT) { _ ->
                println("연결됨")
                it.resume(Unit)
            }
        }
    }

    private suspend fun disconnect() {
        socketClient.disconnect()
        return suspendCoroutine {
            socketClient.once(Socket.EVENT_DISCONNECT) { _ ->
                it.resume(Unit)
            }
        }
    }

    private suspend fun send(method: Method, args: Array<Any>): Any {
        require(args.size > 1) { "@Send method should have one parameter" }
        val event = (method.annotations[0] as Emit).value
        val json = JSONObject(Gson().toJson(args[0]))
        return suspendCoroutine {
            socketClient.emit(event, json, object : Ack {
                override fun call(vararg args: Any?) {
                    if (args.isNullOrEmpty()) it.resume(Unit)
                    else if (method.returnType != Void::class.java) {
                        it.resume(gson.fromJson(args[0].toString(), method.returnType))
                    }
                }
            })
        }
    }

    private fun receive(method: Method): Any {
        require(method.returnType == Flow::class.java) { "@Receive method should return coroutine Flow." }
        val event = (method.annotations[0] as On).value
        val genericType = (method.genericReturnType as ParameterizedType).actualTypeArguments[0]
        return callbackFlow<Any> {
            socketClient.on(event) {
                if (genericType != Unit.javaClass)
                    trySend(gson.fromJson(it[0].toString(), genericType))
                else trySend(Unit)
            }
            awaitClose()
        }
    }

    private fun receiveOnce(method: Method): Any {
        require(method.returnType == Flow::class.java) { "@ReceiveOnce method should return coroutine Flow." }
        val event = (method.annotations[0] as On).value
        val genericType = (method.genericReturnType as ParameterizedType).actualTypeArguments[0]
        return callbackFlow<Any> {
            socketClient.on(event) {
                if (genericType != Unit.javaClass)
                    trySend(gson.fromJson(it[0].toString(), genericType))
                else trySend(Unit)
                close()
            }
        }
    }
}