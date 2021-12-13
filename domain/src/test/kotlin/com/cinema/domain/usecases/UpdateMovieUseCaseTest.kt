package com.cinema.domain.usecases

import com.cinema.domain.exceptions.MovieUpdatingException
import com.cinema.domain.models.Movie
import com.cinema.domain.models.Schedule
import com.cinema.domain.ports.outbound.IUpdateMoviePort
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.DayOfWeek

class UpdateMovieUseCaseTest {

    private val moviePort: IUpdateMoviePort = mockk()
    private val useCase = UpdateMovieUseCase(moviePort)

    // TODO: this could be in a Object Mother
    private val schedules = listOf(Schedule(dayOfWeek = DayOfWeek.MONDAY, times = listOf("10:30")))
    private val movie = Movie(
        id = "12345",
        title = "title test",
        imdbId = "acbd",
        ticketPrice = BigDecimal.TEN,
        schedules = schedules
    )

    @Test
    fun `given a movie to update no exception is thrown when the operation ends successful`() {
        coEvery { moviePort.updateMovie(movie) } just Runs

        Assertions.assertDoesNotThrow { runBlocking { useCase(movie) } }
    }

    @Test
    fun `given an error updating a movie a MovieUpdatingException is thrown`() {
        val errorMessage = "Error updating movie ${movie.id}"

        coEvery { moviePort.updateMovie(movie) } throws MovieUpdatingException(errorMessage)

        val exception = Assertions.assertThrows(MovieUpdatingException::class.java) {
            runBlocking { useCase(movie) }
        }
        Assertions.assertEquals(errorMessage, exception.message)
    }
}
