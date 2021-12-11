package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.infraestructure.database.DBClient
import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import com.cinema.adapters.outbound.repositories.dto.unBuildKey
import com.cinema.adapters.infraestructure.database.QueryBuilder
import org.slf4j.LoggerFactory

class MovieRepository(private val dbClient: DBClient) : IMovieRepository {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun getMovie(movieID: String): MovieStorage {
        return try {
            log.debug("Getting movie schedule for movieID: $movieID")
            dbClient.getAsync<MovieStorage>(
                MovieStorage.tableName(),
                QueryBuilder().buildFiltersWithKey(MovieStorage.toKey(movieID))
            ).await().unBuildKey()
        } catch (ex: Exception) {
            val errorMessage = "Error getting movie schedules for movieID: $movieID. Error detail: [${ex.message}]"
            log.error(errorMessage)
            throw Exception(errorMessage)
        }
    }
}
