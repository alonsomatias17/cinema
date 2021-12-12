package com.cinema.adapters.inbound.handlers

import com.cinema.adapters.inbound.handlers.MovieHandler.Companion.MOVIE_ID
import com.cinema.domain.models.Movie
import com.cinema.domain.models.Schedule
import com.cinema.domain.ports.inbound.IGetMovieByIDPort
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.DayOfWeek

class MovieHandlerTest {

    private val getMovieByID: IGetMovieByIDPort = mockk()
    private val movieHandler = MovieHandler(getMovieByID)

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
}
