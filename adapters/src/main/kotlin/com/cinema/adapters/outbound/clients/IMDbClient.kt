package com.cinema.adapters.outbound.clients

import com.cinema.adapters.infraestructure.httpClient.AbstractClient
import com.cinema.adapters.infraestructure.httpClient.Client
import com.cinema.adapters.outbound.repositories.dto.IMDbResponse

class IMDbClient(
    val secret: String = "",
    override val host: String,
    override val client: Client
) : AbstractClient(host, client) {

    override val clientName: String
        get() = "imdb"

    companion object {
        const val IMDB_ID = "i"
        const val IMDB_KEY = "apikey"
    }

    suspend fun getIMDbDetails(imdbID: String): IMDbResponse {
        // TODO: move to env
        val params = mapOf(IMDB_KEY to "e4f33820", IMDB_ID to imdbID)
        return this.get(uri = "/", params = params)
    }
}
