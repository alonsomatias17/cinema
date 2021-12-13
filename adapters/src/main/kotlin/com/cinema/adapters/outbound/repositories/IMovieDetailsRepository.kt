package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.outbound.repositories.dto.IMDbResponse

interface IMovieDetailsRepository {
    suspend fun getMovieDetailsByImdbID(imdbID: String): IMDbResponse
}
