package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.infraestructure.database.DBClient
import com.cinema.adapters.infraestructure.database.QueryBuilder
import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import com.cinema.adapters.outbound.repositories.dto.buildKey
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
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
        val query = QueryBuilder().buildWithKey(MovieStorage.toKey(movieID), MovieStorage.toSortKey(movieID))

        coEvery { tableMock.getItem(query.getKey()) } returns CompletableFuture.completedFuture(expectedResponse)

        val result = runBlocking { repository.getMovie(movieID) }

        Assertions.assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { tableMock.getItem(query.getKey()) }
    }

    @Test
    fun `given an error getting a movie by id an Exception is thrown`() {
        val movieID = UUID.randomUUID().toString()
        val errorMessage = "Error getting movie"
        val query = QueryBuilder().buildWithKey(MovieStorage.toKey(movieID), MovieStorage.toSortKey(movieID))

        coEvery { tableMock.getItem(query.getKey()) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(Exception::class.java) {
            runBlocking { repository.getMovie(movieID) }
        }
        Assertions.assertEquals(
            "Error getting movie schedules for movieID: $movieID. Error detail [$errorMessage]",
            exception.message
        )
        coVerify(exactly = 1) { tableMock.getItem(query.getKey()) }
    }

    @Test
    fun `given a movie dto the movie is successfully saved without errors`() {
        val movieToSave = MovieStorage(id = UUID.randomUUID().toString())

        coEvery { tableMock.updateItem(movieToSave.buildKey()) } returns
                CompletableFuture.completedFuture(movieToSave.buildKey())

        Assertions.assertDoesNotThrow { runBlocking { repository.updateMovie(movieToSave) } }
        coVerify(exactly = 1) { tableMock.updateItem(movieToSave.buildKey()) }
    }

    @Test
    fun `given an error updating a movie an Exception is thrown`() {
        val movieToSave = MovieStorage(id = UUID.randomUUID().toString())
        val errorMessage = "Error getting movie"

        coEvery { tableMock.updateItem(movieToSave.buildKey()) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(Exception::class.java) {
            runBlocking { repository.updateMovie(movieToSave) }
        }
        Assertions.assertEquals(
            "Error updating movie ${movieToSave.id}. Error detail [$errorMessage]",
            exception.message
        )
        coVerify(exactly = 1) { tableMock.updateItem(movieToSave.buildKey()) }
    }
}
