package com.cinema.adapters.outbound.gateways

import com.cinema.adapters.outbound.gateways.mappers.toDomain
import com.cinema.adapters.outbound.repositories.IMovieRepository
import com.cinema.adapters.util.handleFailure
import com.cinema.domain.exceptions.MovieFetchingException
import com.cinema.domain.exceptions.MovieMappingException
import com.cinema.domain.models.Movie
import com.cinema.domain.ports.outbound.IGetMoviePort

class MovieGateway(private val movieRepository: IMovieRepository) : IGetMoviePort {

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
}
