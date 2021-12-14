package com.cinema.adapters.outbound.gateways

import com.cinema.adapters.outbound.repositories.IRatingRepository
import com.cinema.adapters.util.handleFailure
import com.cinema.domain.exceptions.MovieRatingException
import com.cinema.domain.models.RatingScore
import com.cinema.domain.ports.outbound.IAddMovieRatingPort

class MovieRatingGateway(private val ratingRepository: IRatingRepository) : IAddMovieRatingPort {

    private val className = this::class.java.name

    override suspend fun addRating(movieID: String, rating: RatingScore) {
        kotlin.runCatching { ratingRepository.addRatingToMovie(movieID, rating) }.also {
            it.handleFailure("Error adding rating to movie $movieID", className) { message: String ->
                throw MovieRatingException(message)
            }
        }
    }
}
