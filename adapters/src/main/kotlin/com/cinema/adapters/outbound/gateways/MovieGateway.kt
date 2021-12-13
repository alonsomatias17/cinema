package com.cinema.adapters.outbound.gateways

import com.cinema.adapters.outbound.gateways.mappers.toDomain
import com.cinema.adapters.outbound.gateways.mappers.toStorage
import com.cinema.adapters.outbound.repositories.IMovieRepository
import com.cinema.adapters.util.handleFailure
import com.cinema.domain.exceptions.MovieFetchingException
import com.cinema.domain.exceptions.MovieMappingException
import com.cinema.domain.exceptions.MovieUpdatingException
import com.cinema.domain.models.Movie
import com.cinema.domain.ports.outbound.IGetMoviePort
import com.cinema.domain.ports.outbound.IUpdateMoviePort

class MovieGateway(private val movieRepository: IMovieRepository) : IGetMoviePort, IUpdateMoviePort {

    private val className = this::class.java.name

    override suspend fun getMovieByID(movieID: String): Movie {
        val movie = kotlin.runCatching { movieRepository.getMovie(movieID) }.also {
            it.handleFailure("Error getting movie form repository $movieID", className) { message: String ->
                throw MovieFetchingException(message)
            }
        }

        return kotlin.runCatching { movie.getOrNull()!!.toDomain() }.also {
            it.handleFailure("Error mapping movie $movieID to domain", className) { message: String ->
                throw MovieMappingException(message)
            }
        }.getOrNull()!!
    }

    override suspend fun updateMovie(movie: Movie) {
        kotlin.runCatching { movieRepository.updateMovie(movie.toStorage()) }.also {
            it.handleFailure("Error updating movie ${movie.id}", className) { message: String ->
                throw MovieUpdatingException(message)
            }
        }
    }
}
