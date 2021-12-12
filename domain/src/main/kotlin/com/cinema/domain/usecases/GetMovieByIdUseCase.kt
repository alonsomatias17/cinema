package com.cinema.domain.usecases

import com.cinema.domain.models.Movie
import com.cinema.domain.ports.inbound.IGetMovieByIDPort
import com.cinema.domain.ports.outbound.IGetMoviePort

class GetMovieByIdUseCase(private val moviePort: IGetMoviePort) : IGetMovieByIDPort {
    override suspend fun invoke(movieID: String): Movie {
        return moviePort.getMovieByID(movieID)
    }
}
