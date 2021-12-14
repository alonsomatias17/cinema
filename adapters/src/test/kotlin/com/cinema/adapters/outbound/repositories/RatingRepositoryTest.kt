package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.infraestructure.database.DBClient
import com.cinema.adapters.infraestructure.database.QueryBuilder
import com.cinema.adapters.outbound.gateways.mappers.toStorage
import com.cinema.adapters.outbound.repositories.dto.RatingStorage
import com.cinema.adapters.outbound.repositories.dto.ScoreStorage
import com.cinema.adapters.outbound.repositories.dto.buildKey
import com.cinema.domain.models.RatingScore
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import java.util.UUID
import java.util.concurrent.CompletableFuture

// TODO: Test error branches
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RatingRepositoryTest {

    private val dynamoClientMock: DynamoDbEnhancedAsyncClient = mockk()
    private val tableMock: DynamoDbAsyncTable<RatingStorage> = mockk()
    private val dbClientMock = DBClient(dynamoClientMock, mapOf(RatingStorage.entityName() to tableMock))
    private val repository = RatingRepository(dbClientMock)

    @BeforeEach
    fun setUpEach() {
        clearAllMocks()
    }

    @Test
    fun `given a rating score for a already existing saved ratings, the new rating is added to de list and updated`() {
        val ratingScore = RatingScore("John", 3)
        val movieID = UUID.randomUUID().toString()
        val query = QueryBuilder().buildWithKey(RatingStorage.toKey(movieID), RatingStorage.toSortKey(movieID))
        val ratingStorage = RatingStorage(
            movieId = "mo#12345",
            sortKey = "ra#12345",
            scores = listOf(ScoreStorage("Brenda", 3))
        )
        val ratingToSave = ratingStorage.copy(scores = ratingStorage.scores.plus(ratingScore.toStorage()))

        coEvery { tableMock.getItem(query.getKey()) } returns CompletableFuture.completedFuture(ratingStorage)
        coEvery { tableMock.updateItem(ratingToSave) } returns CompletableFuture.completedFuture(ratingStorage)

        Assertions.assertDoesNotThrow { runBlocking { repository.addRatingToMovie(movieID, ratingScore) } }
        coVerify(exactly = 1) {
            tableMock.getItem(query.getKey())
            tableMock.updateItem(ratingToSave)
        }
        coVerifyOrder {
            tableMock.getItem(query.getKey())
            tableMock.updateItem(ratingToSave)
        }
    }

    @Test
    fun `given a rating score for a non existing saved ratings, the new rating is created`() {
        val ratingScore = RatingScore("John", 3)
        val movieID = UUID.randomUUID().toString()
        val ratingToSave = RatingStorage(movieId = movieID, scores = listOf(ratingScore.toStorage())).buildKey()
        val query = QueryBuilder().buildWithKey(RatingStorage.toKey(movieID), RatingStorage.toSortKey(movieID))

        coEvery { tableMock.getItem(query.getKey()) } returns CompletableFuture.completedFuture(null)
        coEvery { tableMock.putItem(ratingToSave) } returns CompletableFuture.completedFuture(null)

        Assertions.assertDoesNotThrow { runBlocking { repository.addRatingToMovie(movieID, ratingScore) } }
        coVerify(exactly = 1) {
            tableMock.getItem(query.getKey())
            tableMock.putItem(ratingToSave)
        }
        coVerifyOrder {
            tableMock.getItem(query.getKey())
            tableMock.putItem(ratingToSave)
        }
    }
}
