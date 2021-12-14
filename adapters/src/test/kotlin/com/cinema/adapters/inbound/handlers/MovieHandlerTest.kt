package com.cinema.adapters.inbound.handlers

import com.cinema.adapters.inbound.handlers.MovieHandler.Companion.MOVIE_ID
import com.cinema.adapters.infraestructure.httpClient.Mapper
import com.cinema.domain.models.Movie
import com.cinema.domain.models.Schedule
import com.cinema.domain.ports.inbound.IGetMovieByIDPort
import com.cinema.domain.ports.inbound.IGetMovieDetailsByIDPort
import com.cinema.domain.ports.inbound.IRateMoviePort
import com.cinema.domain.ports.inbound.IUpdateMoviePort
import io.ktor.features.BadRequestException
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.DayOfWeek

class MovieHandlerTest {

    private val getMovieByID: IGetMovieByIDPort = mockk()
    private val getMovieDetailsByID: IGetMovieDetailsByIDPort = mockk()
    private val updateMovie: IUpdateMoviePort = mockk()
    private val rateMovie: IRateMoviePort = mockk()
    private val movieHandler = MovieHandler(getMovieByID, getMovieDetailsByID, updateMovie, rateMovie)

    private val schedules = listOf(Schedule(dayOfWeek = DayOfWeek.MONDAY, times = listOf("10:30")))
    private val movie = Movie(
        id = "12345",
        title = "title test",
        imdbId = "acbd",
        ticketPrice = BigDecimal.TEN,
        schedules = schedules
    )

    @Test
    fun `when getting movie schedules, given a movie id, schedules are returned`() {
        val parameters = mapOf(MOVIE_ID to movie.id)

        coEvery { getMovieByID(movie.id) } returns movie

        val result = runBlocking { movieHandler.getSchedules(parameters) }

        Assertions.assertEquals(movie.schedules, result)
    }

    @Test
    fun `when getting movie schedules, given a an error, an Exception is thrown`() {
        val errorMessage = "Error getting schedules"
        val parameters = mapOf(MOVIE_ID to movie.id)

        coEvery { getMovieByID(movie.id) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(Exception::class.java) {
            runBlocking { movieHandler.getSchedules(parameters) }
        }
        Assertions.assertEquals("Error getting movie schedules. Error detail [$errorMessage]", exception.message)
    }

    @Test
    fun `when getting a movie, given a movie id, a movie is returned`() {
        val parameters = mapOf(MOVIE_ID to movie.id)

        coEvery { getMovieByID(movie.id) } returns movie

        val result = runBlocking { movieHandler.getMovie(parameters) }

        Assertions.assertEquals(movie, result)
    }

    @Test
    fun `when getting a movie, given a an error, an Exception is thrown`() {
        val errorMessage = "Error getting movie"
        val parameters = mapOf(MOVIE_ID to movie.id)

        coEvery { getMovieByID(movie.id) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(Exception::class.java) {
            runBlocking { movieHandler.getMovie(parameters) }
        }
        Assertions.assertEquals("Error getting movie by ID. Error detail [$errorMessage]", exception.message)
    }

    @Test
    fun `given a movie to update no exception is thrown when the operation ends successful`() {
        val body = getMovieUpdateBodyMock()
        val movie = Mapper.secondaryCamelCaseMapper().deserialize<Movie>(body)

        coEvery { updateMovie(movie) } just Runs

        Assertions.assertDoesNotThrow { runBlocking { movieHandler.updateMovie(body) } }
    }

    @Test
    fun `given a an error updating a movie an Exception is thrown`() {
        val errorMessage = "Error updating movie"
        val body = getMovieUpdateBodyMock()
        val movie = Mapper.secondaryCamelCaseMapper().deserialize<Movie>(body)

        coEvery { updateMovie(movie) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(Exception::class.java) {
            runBlocking { movieHandler.updateMovie(body) }
        }
        Assertions.assertEquals("Error updating movie ${movie.id}. Error detail [$errorMessage]", exception.message)
    }

    @Test
    fun `when updating a movie given an error in the request body an Exception is thrown`() {
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            runBlocking { movieHandler.updateMovie("body") }
        }
        Assertions.assertTrue(
            exception.message?.contains("Error invalid movie body. Error detail [Deserialization error") ?: false
        )
    }

    private fun getMovieUpdateBodyMock() =
        this.javaClass.getResource("/mocks/movie-update-body.json")!!.readText()
}
