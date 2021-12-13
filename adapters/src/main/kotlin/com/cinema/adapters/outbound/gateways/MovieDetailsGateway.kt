package com.cinema.adapters.outbound.gateways

import com.cinema.adapters.outbound.gateways.mappers.toDomain
import com.cinema.adapters.outbound.repositories.IMovieDetailsRepository
import com.cinema.adapters.outbound.repositories.IMovieRepository
import com.cinema.adapters.util.handleFailure
import com.cinema.domain.exceptions.MovieDetailsFetchingException
import com.cinema.domain.exceptions.MovieFetchingException
import com.cinema.domain.models.MovieDetails
import com.cinema.domain.ports.outbound.IGetMovieDetailsPort

class MovieDetailsGateway(
    private val movieRepository: IMovieRepository,
    private val movieDetailsRepository: IMovieDetailsRepository
) : IGetMovieDetailsPort {

    private val className = this::class.java.name

    override suspend fun getMovieDetailsByID(movieID: String): MovieDetails {
        val movie = kotlin.runCatching { movieRepository.getMovie(movieID) }.also {
            it.handleFailure("Error getting movie form repository $movieID", className) { message: String ->
                throw MovieFetchingException(message)
            }
        }

        val movieDetails = kotlin.runCatching {
            movieDetailsRepository.getMovieDetailsByImdbID(movie.getOrNull()!!.imdbId)
        }.also {
            it.handleFailure("Error getting movie details form repository $movieID", className) { message: String ->
                throw MovieDetailsFetchingException(message)
            }
        }.getOrNull()!!

        return movieDetails.toDomain()
    }
}
