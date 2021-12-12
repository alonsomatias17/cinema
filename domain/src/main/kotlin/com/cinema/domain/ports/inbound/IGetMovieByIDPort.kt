package com.cinema.domain.ports.inbound

import com.cinema.domain.models.Movie

interface IGetMovieByIDPort {
    suspend operator fun invoke(movieID: String): Movie
}
