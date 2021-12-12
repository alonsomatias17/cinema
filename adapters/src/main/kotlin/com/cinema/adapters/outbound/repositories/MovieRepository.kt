package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.infraestructure.database.DBClient
import com.cinema.adapters.infraestructure.database.QueryBuilder
import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import com.cinema.adapters.outbound.repositories.dto.buildKey
import com.cinema.adapters.outbound.repositories.dto.unBuildKey
import com.cinema.adapters.util.handleFailure
import org.slf4j.LoggerFactory

class MovieRepository(private val dbClient: DBClient) : IMovieRepository {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val className = this::class.java.name

    override suspend fun getMovie(movieID: String): MovieStorage {
        log.debug("Getting movie schedule for movieID: $movieID")
        return kotlin.runCatching {
            dbClient.getAsync<MovieStorage>(
                MovieStorage.tableName(),
                QueryBuilder().buildWithKey(MovieStorage.toKey(movieID), MovieStorage.toSortKey(movieID))
            ).await().unBuildKey()
        }.also {
            it.handleFailure("Error getting movie schedules for movieID: $movieID", className) { message: String ->
                throw Exception(message)
            }
        }.getOrNull()!!
    }

    override suspend fun updateMovie(movie: MovieStorage) {
        log.debug("Updating movie ${movie.id}")
        kotlin.runCatching { dbClient.updateAsync(MovieStorage.tableName(), movie.buildKey()).await() }.also {
            it.handleFailure("Error updating movie ${movie.id}", className) { message: String ->
                throw Exception(message)
            }
        }
    }
}
