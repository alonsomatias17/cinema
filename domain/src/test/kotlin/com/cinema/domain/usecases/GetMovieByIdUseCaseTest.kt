package com.cinema.domain.usecases

import com.cinema.domain.exceptions.MovieFetchingException
import com.cinema.domain.models.Movie
import com.cinema.domain.models.Schedule
import com.cinema.domain.ports.outbound.IGetMoviePort
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.DayOfWeek

class GetMovieByIdUseCaseTest {

    private val moviePort: IGetMoviePort = mockk()
    private val useCase = GetMovieByIdUseCase(moviePort)

    private val schedules = listOf(Schedule(dayOfWeek = DayOfWeek.MONDAY, times = listOf("10:30")))
    private val movie = Movie(
        id = "12345",
        title = "title test",
        imdbId = "acbd",
        ticketPrice = BigDecimal.TEN,
        schedules = schedules
    )

    @Test
    fun `given a movie id a movie is returned`() {
        coEvery { moviePort.getMovieByID(movie.id) } returns movie

        val result = runBlocking { useCase(movie.id) }

        Assertions.assertEquals(movie, result)
    }

    @Test
    fun `given an error a movie is returned`() {
        val errorMessage = "Error getting movie"

        coEvery { moviePort.getMovieByID(movie.id) } throws MovieFetchingException(errorMessage)

        val exception = Assertions.assertThrows(MovieFetchingException::class.java) {
            runBlocking { useCase(movie.id) }
        }
        Assertions.assertEquals(errorMessage, exception.message)
    }
}
