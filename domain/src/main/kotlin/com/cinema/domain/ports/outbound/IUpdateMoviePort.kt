package com.cinema.domain.ports.outbound

import com.cinema.domain.models.Movie

interface IUpdateMoviePort {
    suspend fun updateMovie(movie: Movie)
}
