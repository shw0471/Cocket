package com.semicolon.cocket

import com.google.gson.Gson
import com.semicolon.cocket.annotation.*
import io.socket.client.Ack
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.Transport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CocketExecutor(
    private val socketClient: Socket
) {
    private val gson = Gson()

    @Suppress("UNCHECKED_CAST")
    fun execute(method: Method, args: Array<Any>): Any {
        val continuation = args.lastOrNull() as? Continuation<Any>
        if (continuation != null) {
            CoroutineScope(Dispatchers.IO).launch {
                continuation.resume(executeSuspend(method, args))
            }
        } else {
            return execute(method)
        }
        return COROUTINE_SUSPENDED
    }

    private suspend fun executeSuspend(method: Method, args: Array<Any>): Any {
        require(method.annotations.size == 1) { "There should be only one annotation." }
        return when (method.annotations.first()) {
            is Connect -> connect(method, args)
            is Disconnect -> disconnect()
            is Emit -> emit(method, args)
            is Off -> off(method)
            else -> throw IllegalArgumentException("invalid annotation.")
        }
    }

    private fun execute(method: Method): Any {
        require(method.annotations.size == 1) { "There should be only one annotation." }
        return when (method.annotations.first()) {
            is On -> on(method)
            is Once -> once(method)
            is Off -> off(method)
            else -> throw IllegalArgumentException("invalid annotation.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun connect(method: Method, args: Array<Any>) {
        val headers = mutableMapOf<String, String>()
        method.parameterAnnotations.mapIndexed { index, arrayOfAnnotations ->
            if (arrayOfAnnotations.firstOrNull() is Header)
                headers[(arrayOfAnnotations[0] as Header).value] = args[index].toString()
        }
        socketClient.io().on(Manager.EVENT_TRANSPORT) {
            (it[0] as Transport).on(Transport.EVENT_REQUEST_HEADERS) { args ->
                val requestHeaders = args[0] as MutableMap<String, List<String>>
                headers.map { header -> requestHeaders.put(header.key, listOf(header.value)) }
            }
        }
        socketClient.connect()
        return suspendCoroutine {
            socketClient.once(Socket.EVENT_CONNECT) { _ ->
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

    private suspend fun emit(method: Method, args: Array<Any>): Any {
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

    private fun on(method: Method): Any {
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

    private fun once(method: Method): Any {
        require(method.returnType == Flow::class.java) { "@ReceiveOnce method should return coroutine Flow." }
        val event = (method.annotations[0] as Once).value
        val genericType = (method.genericReturnType as ParameterizedType).actualTypeArguments[0]
        return callbackFlow<Any> {
            socketClient.once(event) {
                if (genericType != Unit.javaClass)
                    trySend(gson.fromJson(it[0].toString(), genericType))
                else trySend(Unit)
                close()
            }
        }
    }

    private fun off(method: Method) {
        val event = (method.annotations[0] as Off).value
        if (event.isEmpty()) {
            socketClient.off()
        } else {
            socketClient.off(event)
        }
    }
}