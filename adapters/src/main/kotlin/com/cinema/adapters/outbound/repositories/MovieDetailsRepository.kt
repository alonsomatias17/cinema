package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.outbound.clients.IMDbClient
import com.cinema.adapters.outbound.repositories.dto.IMDbResponse
import com.cinema.adapters.util.handleFailure
import org.slf4j.LoggerFactory

class MovieDetailsRepository(private val client: IMDbClient) : IMovieDetailsRepository {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val className = this::class.java.name

    override suspend fun getMovieDetailsByImdbID(imdbID: String): IMDbResponse {
        log.debug("Getting movie details for imdbID: $imdbID")
        return kotlin.runCatching { client.getIMDbDetails(imdbID) }.also {
            it.handleFailure("Error getting movie details for imdbID: $imdbID", className) { message: String ->
                throw Exception(message)
            }
        }.getOrNull()!!
    }
}
