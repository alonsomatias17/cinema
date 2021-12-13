package com.cinema.adapters.inbound.handlers

import com.cinema.adapters.util.handleFailure
import com.cinema.domain.models.Movie
import com.cinema.domain.models.MovieDetails
import com.cinema.domain.models.Schedule
import com.cinema.domain.ports.inbound.IGetMovieByIDPort
import com.cinema.domain.ports.inbound.IGetMovieDetailsByIDPort

class MovieHandler(
    private val getMovieByID: IGetMovieByIDPort,
    private val getMovieDetailsByID: IGetMovieDetailsByIDPort
) {

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

    suspend fun getDetails(params: Map<String, String>): MovieDetails {
        return kotlin.runCatching { getMovieDetailsByID(params[MOVIE_ID]!!) }.also {
            it.handleFailure("Error getting movie details by movie ID", className) { message: String ->
                throw Exception(message)
            }
        }.getOrNull()!!
    }

    fun rate(body: Map<String, Any>): Any {
        TODO("Not yet implemented")
    }

    fun update(body: Map<String, Any>): Any {
        TODO("Not yet implemented")
    }
}
