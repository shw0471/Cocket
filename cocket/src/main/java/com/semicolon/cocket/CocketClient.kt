package com.semicolon.cocket

import io.socket.client.IO
import java.lang.reflect.Proxy

class CocketClient internal constructor(
    private val cocketExecutor: CocketExecutor
) {

    fun <T : Any> create(service: Class<T>): T {
        val proxyInstance = Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service)
        ) { _, method, arguments ->
            cocketExecutor.execute(method, arguments ?: arrayOf())
        }
        return service.cast(proxyInstance) ?: throw TypeCastException()
    }

    class Builder {
        private var baseUrl: String = ""
        private var options = IO.Options()

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