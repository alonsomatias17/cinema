package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.infraestructure.database.DBClient
import com.cinema.adapters.infraestructure.database.QueryBuilder
import com.cinema.adapters.outbound.gateways.mappers.toStorage
import com.cinema.adapters.outbound.repositories.dto.RatingStorage
import com.cinema.adapters.outbound.repositories.dto.buildKey
import com.cinema.adapters.util.handleFailure
import com.cinema.domain.models.RatingScore
import org.slf4j.LoggerFactory
import java.util.Objects.nonNull

class RatingRepository(private val dbClient: DBClient) : IRatingRepository {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val className = this::class.java.name

    override suspend fun addRatingToMovie(movieID: String, rating: RatingScore) {
        log.debug("Adding rating to movie $movieID")
        val query = QueryBuilder().buildWithKey(RatingStorage.toKey(movieID), RatingStorage.toSortKey(movieID))

        kotlin.runCatching {
            val ratingStorage = dbClient.getAsync<RatingStorage?>(RatingStorage.entityName(), query).await()

            if (alreadyExist(ratingStorage)) {
                dbClient.updateAsync(
                    RatingStorage.entityName(),
                    ratingStorage!!.copy(scores = ratingStorage.scores.plus(rating.toStorage()))
                ).await()
            } else {
                dbClient.saveAsync(
                    RatingStorage.entityName(),
                    RatingStorage(movieId = movieID, scores = listOf(rating.toStorage())).buildKey()
                ).await()
            }
        }.also {
            it.handleFailure("Error adding rating to movie $movieID", className) { message: String ->
                throw Exception(message)
            }
        }
    }

    private fun alreadyExist(ratingStorage: RatingStorage?) = nonNull(ratingStorage)
}
