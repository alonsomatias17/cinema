package com.cinema.adapters.outbound.repositories

import com.cinema.domain.models.RatingScore

interface IRatingRepository {
    suspend fun addRatingToMovie(movieId: String, rating: RatingScore)
}
