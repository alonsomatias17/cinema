package com.cinema.application.routes

import com.cinema.adapters.inbound.handlers.MovieHandler
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.util.StringValues
import org.koin.ktor.ext.inject

fun Routing.moviesRoutes() {
    val movieHandler by inject<MovieHandler>()

    get("/v1/cinema/movies/{movieID}/schedules") {
        call.respond(movieHandler.getSchedules(call.parameters.map()))
    }

    get("/v1/cinema/movies/{movieID}/details") {
        call.respond(movieHandler.getDetails(call.parameters.map()))
    }

    get("/v1/cinema/movies/{movieID}") {
        call.respond(movieHandler.getMovie(call.parameters.map()))
    }

    // TODO: check token
    post("/v1/cinema/movies/{movieID}/rate") {
        call.respond(movieHandler.rate(call.receive()))
    }

    // TODO: check token
    post("/v1/cinema/movies") {
        call.respond(movieHandler.updateMovie(call.receive()))
    }
}

private fun StringValues.map(): Map<String, String> =
    this.entries().associateBy({ it.key }, { it.value.joinToString() })
