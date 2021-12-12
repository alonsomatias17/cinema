package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.infraestructure.database.DBClient
import com.cinema.adapters.infraestructure.database.QueryBuilder
import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import io.mockk.clearAllMocks
import io.mockk.coEvery
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovieRepositoryTest {

    private val dynamoClientMock: DynamoDbEnhancedAsyncClient = mockk()
    private val tableMock: DynamoDbAsyncTable<MovieStorage> = mockk()
    private val dbClientMock = DBClient(dynamoClientMock, mapOf(MovieStorage.tableName() to tableMock))
    private val repository = MovieRepository(dbClientMock)

    @BeforeEach
    fun setUpEach() {
        clearAllMocks()
    }

    @Test
    fun `given a movie id a movie is returned`() {
        val movieID = UUID.randomUUID().toString()
        val expectedResponse = MovieStorage(id = movieID)
        val query = QueryBuilder().buildFiltersWithKey(MovieStorage.toKey(movieID))

        coEvery { tableMock.getItem(query.getKey()) } returns CompletableFuture.completedFuture(expectedResponse)

        val result = runBlocking { repository.getMovie(movieID) }

        Assertions.assertEquals(expectedResponse, result)
    }

    @Test
    fun `given an error getting a movie by id an Exception is thrown`() {
        val movieID = UUID.randomUUID().toString()
        val errorMessage = "Error getting movie"
        val query = QueryBuilder().buildFiltersWithKey(MovieStorage.toKey(movieID))

        coEvery { tableMock.getItem(query.getKey()) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(Exception::class.java) {
            runBlocking { repository.getMovie(movieID) }
        }
        Assertions.assertEquals(
            "Error getting movie schedules for movieID: $movieID. Error detail: [$errorMessage]",
            exception.message
        )
    }
}
