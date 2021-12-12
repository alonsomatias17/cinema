package com.cinema.domain.ports.outbound

import com.cinema.domain.models.Movie

interface IGetMoviePort {
    suspend fun getMovieByID(movieID: String): Movie
}
