package com.cinema.adapters.inbound.handlers

import com.cinema.adapters.util.handleFailure
import com.cinema.domain.models.Movie
import com.cinema.domain.models.Schedule
import com.cinema.domain.ports.inbound.IGetMovieByIDPort

class MovieHandler(private val getMovieByID: IGetMovieByIDPort) {

    companion object {
        const val MOVIE_ID = "movieID"
    }

    private val className = this::class.java.name

    suspend fun getSchedules(params: Map<String, String>): List<Schedule> {
        return kotlin.runCatching { getMovieByID(params[MOVIE_ID]!!) }.also {
            it.handleFailure("Error getting movie schedules", className) { message: String ->
                throw Exception(message)
            }
        }.getOrNull()!!.schedules
    }

    suspend fun getMovie(params: Map<String, String>): Movie {
        return kotlin.runCatching { getMovieByID(params[MOVIE_ID]!!) }.also {
            it.handleFailure("Error getting movie by ID", className) { message: String ->
                throw Exception(message)
            }
        }.getOrNull()!!
    }

    fun getDetails(params: Map<String, String>): Any {
        TODO("Not yet implemented")
    }

    fun rate(body: Map<String, Any>): Any {
        TODO("Not yet implemented")
    }

    fun update(body: Map<String, Any>): Any {
        TODO("Not yet implemented")
    }
}
