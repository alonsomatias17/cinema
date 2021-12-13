package com.cinema.domain.usecases

import com.cinema.domain.models.Movie
import com.cinema.domain.ports.inbound.IUpdateMoviePort
import com.cinema.domain.ports.outbound.IUpdateMoviePort as IUpdateMovieOutboundPort

class UpdateMovieUseCase(private val moviePort: IUpdateMovieOutboundPort) : IUpdateMoviePort {
    override suspend fun invoke(movie: Movie) {
        moviePort.updateMovie(movie)
    }
}
