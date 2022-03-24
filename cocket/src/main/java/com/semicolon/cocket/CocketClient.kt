package com.semicolon.cocket

import io.socket.client.IO
import kotlinx.coroutines.*
import java.lang.reflect.Proxy
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class CocketClient internal constructor(
    private val cocketExecutor: CocketExecutor
) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(service: Class<T>): T {
        val proxyInstance = Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service)
        ) { _, method, arguments ->
            val args = arguments ?: arrayOf()
            val continuation = args.lastOrNull() as? Continuation<Any>
            if (continuation != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    continuation.resume(cocketExecutor.executeSuspend(method, args))
                }
            } else {
                return@newProxyInstance cocketExecutor.execute(method)
            }
            COROUTINE_SUSPENDED
        }
        return service.cast(proxyInstance) ?: throw TypeCastException()
    }

    class Builder {
        private lateinit var baseUrl: String
        private lateinit var options: IO.Options

        fun baseUrl(baseUrl: String) =
            apply {
                this.baseUrl = baseUrl
            }

        fun options(options: IO.Options) =
            apply { this.options = options }

        fun build() = CocketClient(
            CocketExecutor(IO.socket(baseUrl, options))
        )
    }
}