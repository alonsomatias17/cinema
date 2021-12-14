package com.cinema.domain.ports.inbound

import com.cinema.domain.models.RatingScore

interface IRateMoviePort {
    suspend operator fun invoke(movieID: String, rating: RatingScore)
}
