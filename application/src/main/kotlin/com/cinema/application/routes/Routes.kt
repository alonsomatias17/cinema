package com.cinema.application.routes

import io.ktor.routing.Routing

fun Routing.routes() {
    healthCheckRoute()
    moviesRoutes()
}
