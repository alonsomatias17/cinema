package com.cinema.application

import com.cinema.application.modules.ModuleLoader
import com.cinema.application.routes.routes
import com.papsign.ktor.openapigen.OpenAPIGen
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import org.koin.ktor.ext.Koin

fun Application.features() {
    install(DefaultHeaders)

    install(Compression) {
        gzip()
        deflate()
    }

    install(ContentNegotiation) {
        jackson()
    }

    install(OpenAPIGen)

    install(CallLogging)

    install(Koin) {
        modules(ModuleLoader.modules)
    }

    install(Routing) {
        routes()
    }
}
