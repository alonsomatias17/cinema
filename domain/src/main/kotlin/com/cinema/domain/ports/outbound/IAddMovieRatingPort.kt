package com.cinema.domain.ports.outbound

import com.cinema.domain.models.RatingScore

interface IAddMovieRatingPort {
    suspend fun addRating(movieID: String, rating: RatingScore)
}