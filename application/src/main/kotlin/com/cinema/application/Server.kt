package com.cinema.application

import com.cinema.application.configuration.Config
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
                this.runningLimit = Config.get("ktor.configure.runningLimit")
                this.requestQueueLimit = Config.get("ktor.configure.requestQueueLimit")
                this.tcpKeepAlive = false
                this.responseWriteTimeoutSeconds = WRITE_TO
            },
            port = Config.get("ktor.deployment.port"),
            watchPaths = Config.get("ktor.watch"),
            module = module()
        )
    }

    private fun module(): Application.() -> Unit {
        return Application::main
    }
}

fun Application.main() {
    features()
    exceptions()
}
