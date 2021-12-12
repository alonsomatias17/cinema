package com.cinema.application.routes

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route

fun Routing.healthCheckRoute() {
    route("/") {
        get {
            call.respondText("OK")
        }
    }
}
