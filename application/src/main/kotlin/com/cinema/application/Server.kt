package com.cinema.application

import io.ktor.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

object Server {

    private const val WRITE_TO = 1

    fun config(): ApplicationEngine {
        return embeddedServer(
            Netty,
            configure = {
                this.connectionGroupSize = this.parallelism
                this.workerGroupSize = this.parallelism
                this.callGroupSize = this.parallelism
                this.runningLimit = 150
                this.requestQueueLimit = 300
                this.tcpKeepAlive = false
            },
            port = 8080,
            watchPaths = emptyList(),
            module = module()
        )
    }

    private fun module(): Application.() -> Unit {
        return Application::main
    }
}

fun Application.main() {
//    features()
//    exceptions()
}
