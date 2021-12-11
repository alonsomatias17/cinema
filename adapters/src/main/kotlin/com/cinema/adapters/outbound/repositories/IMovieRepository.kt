package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.outbound.repositories.dto.MovieStorage

interface IMovieRepository {
    suspend fun getMovie(movieID: String): MovieStorage
}
