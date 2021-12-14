package com.cinema.domain.usecases

import com.cinema.domain.models.RatingScore
import com.cinema.domain.ports.inbound.IRateMoviePort
import com.cinema.domain.ports.outbound.IAddMovieRatingPort

/**
 * I will assume as a business rule that the data is valid.
 * That is, the movie ID is correct, as well as the rating value
 */
class RateMovieUseCase(private val ratePort: IAddMovieRatingPort): IRateMoviePort {
    override suspend fun invoke(movieID: String, rating: RatingScore) {
        ratePort.addRating(movieID, rating)
    }
}
