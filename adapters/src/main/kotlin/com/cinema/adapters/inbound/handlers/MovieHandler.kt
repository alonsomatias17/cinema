package com.cinema.adapters.inbound.handlers

import com.cinema.adapters.infraestructure.httpClient.Mapper
import com.cinema.adapters.util.handleFailure
import com.cinema.domain.models.Movie
import com.cinema.domain.models.MovieDetails
import com.cinema.domain.models.Schedule
import com.cinema.domain.ports.inbound.IGetMovieByIDPort
import com.cinema.domain.ports.inbound.IGetMovieDetailsByIDPort
import com.cinema.domain.ports.inbound.IUpdateMoviePort
import io.ktor.features.BadRequestException

class MovieHandler(
    private val getMovieByID: IGetMovieByIDPort,
    private val getMovieDetailsByID: IGetMovieDetailsByIDPort,
    private val updateMovie: IUpdateMoviePort,
    private val mapper: Mapper = Mapper.secondaryCamelCaseMapper()
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

    suspend fun updateMovie(body: String) {
        val movie = kotlin.runCatching { mapper.deserialize<Movie>(body) }.also {
            it.handleFailure("Error invalid movie body", className) { message: String ->
                throw BadRequestException(message)
            }
        }.getOrNull()!!

        kotlin.runCatching { updateMovie(movie) }.also {
            it.handleFailure("Error updating movie ${movie.id}", className) { message: String ->
                throw Exception(message)
            }
        }
    }
}
