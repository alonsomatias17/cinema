package com.cinema.adapters.outbound.gateways

import com.cinema.adapters.outbound.gateways.mappers.toStorage
import com.cinema.adapters.outbound.repositories.IMovieRepository
import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import com.cinema.adapters.outbound.repositories.dto.Schedule
import com.cinema.domain.exceptions.MovieFetchingException
import com.cinema.domain.exceptions.MovieMappingException
import com.cinema.domain.models.Movie
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.DayOfWeek
import java.util.UUID

class MovieGatewayTest {

    private val movieRepository: IMovieRepository = mockk()
    private val gateway = MovieGateway(movieRepository)

    @Test
    fun `given a movie id a domain movie is returned`() {
        val movieSchedules = listOf(Schedule(dayOfWeek = DayOfWeek.MONDAY.name, times = listOf("10:30")))
        val movie = MovieStorage(id = UUID.randomUUID().toString(), schedules = movieSchedules)

        coEvery { movieRepository.getMovie(movie.id) } returns movie

        val result = runBlocking { gateway.getMovieByID(movie.id) }

        Assertions.assertEquals(movie.id, result.id)
    }

    @Test
    fun `given an error getting a movie by id a MovieFetchingException is thrown`() {
        val movieID = UUID.randomUUID().toString()
        val errorMessage = "Error getting movie by id"

        coEvery { movieRepository.getMovie(movieID) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(MovieFetchingException::class.java) {
            runBlocking { gateway.getMovieByID(movieID) }
        }
        Assertions.assertEquals(
            "Error getting movie form repository $movieID. Error detail [$errorMessage]",
            exception.message
        )
    }

    @Test
    fun `given an error mapping to domain movie a MovieMappingException is thrown`() {
        val invalidMovieSchedules = listOf(Schedule(dayOfWeek = "INVALID_DAY", times = listOf("10:30")))
        val movie = MovieStorage(id = UUID.randomUUID().toString(), schedules = invalidMovieSchedules)

        coEvery { movieRepository.getMovie(movie.id) } returns movie

        val exception = Assertions.assertThrows(MovieMappingException::class.java) {
            runBlocking { gateway.getMovieByID(movie.id) }
        }
        Assertions.assertEquals(
            "Error mapping movie ${movie.id} to domain. " +
                    "Error detail [No enum constant java.time.DayOfWeek.INVALID_DAY]", exception.message
        )
    }

    @Test
    fun `given a movie to update no exception is thrown when the operation ends successful`() {
        coEvery { movieRepository.updateMovie(movie.toStorage()) } just Runs

        Assertions.assertDoesNotThrow { runBlocking { gateway.updateMovie(movie) } }
    }

    // TODO: this could be in a Object Mother
    private val schedules = listOf(
        com.cinema.domain.models.Schedule(
            dayOfWeek = DayOfWeek.MONDAY,
            times = listOf("10:30")
        )
    )
    private val movie = Movie(
        id = "12345",
        title = "title test",
        imdbId = "acbd",
        ticketPrice = BigDecimal.TEN,
        schedules = schedules
    )
}
