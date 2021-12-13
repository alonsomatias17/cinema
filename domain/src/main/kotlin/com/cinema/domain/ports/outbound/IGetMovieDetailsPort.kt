package com.cinema.domain.ports.outbound

import com.cinema.domain.models.MovieDetails

interface IGetMovieDetailsPort {
    suspend fun getMovieDetailsByID(movieID: String): MovieDetails
}
