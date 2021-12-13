package com.cinema.domain.usecases

import com.cinema.domain.models.MovieDetails
import com.cinema.domain.ports.inbound.IGetMovieDetailsByIDPort
import com.cinema.domain.ports.outbound.IGetMovieDetailsPort

class GetMovieDetailsByIDUseCase(private val movieDetailsPort: IGetMovieDetailsPort) : IGetMovieDetailsByIDPort {
    override suspend fun invoke(movieID: String): MovieDetails {
        return movieDetailsPort.getMovieDetailsByID(movieID)
    }
}
