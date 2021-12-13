package com.cinema.domain.ports.inbound

import com.cinema.domain.models.Movie

interface IUpdateMoviePort {
    suspend operator fun invoke(movie: Movie)
}
