package com.cinema.domain.ports.inbound

import com.cinema.domain.models.MovieDetails

interface IGetMovieDetailsByIDPort {
     suspend operator fun invoke(movieID: String): MovieDetails
}
