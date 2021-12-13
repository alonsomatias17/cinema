package com.cinema.adapters.outbound.gateways

import com.cinema.adapters.outbound.gateways.mappers.toDomain
import com.cinema.adapters.outbound.repositories.IMovieDetailsRepository
import com.cinema.adapters.outbound.repositories.IMovieRepository
import com.cinema.adapters.outbound.repositories.dto.IMDbResponse
import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import com.cinema.domain.exceptions.MovieDetailsFetchingException
import com.cinema.domain.exceptions.MovieFetchingException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class MovieDetailsGatewayTest {

    private val movieRepository: IMovieRepository = mockk()
    private val movieDetailsRepository: IMovieDetailsRepository = mockk()
    private val gateway = MovieDetailsGateway(movieRepository, movieDetailsRepository)

    @Test
    fun `given a movie id a movie detail is returned`() {
        val movieStorage = MovieStorage(id = UUID.randomUUID().toString(), imdbId = "tt12345")

        coEvery { movieRepository.getMovie(movieStorage.id) } returns movieStorage
        coEvery { movieDetailsRepository.getMovieDetailsByImdbID(movieStorage.imdbId) } returns imbdResponse

        val result = runBlocking { gateway.getMovieDetailsByID(movieStorage.id) }

        Assertions.assertEquals(imbdResponse.toDomain(), result)
    }

    @Test
    fun `given an error getting movie from repository, a MovieFetchingException is thrown`() {
        val movieID = UUID.randomUUID().toString()
        val errorMessage = "Error getting movie by id"

        coEvery { movieRepository.getMovie(movieID) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(MovieFetchingException::class.java) {
            runBlocking { gateway.getMovieDetailsByID(movieID) }
        }
        Assertions.assertEquals(
            "Error getting movie form repository $movieID. Error detail [$errorMessage]",
            exception.message
        )
    }

    @Test
    fun `given an error getting movie details from repository, a MovieDetailsFetchingException is thrown`() {
        val movieStorage = MovieStorage(id = UUID.randomUUID().toString(), imdbId = "tt12345")
        val errorMessage = "Error getting movie details"

        coEvery { movieRepository.getMovie(movieStorage.id) } returns movieStorage
        coEvery { movieDetailsRepository.getMovieDetailsByImdbID(movieStorage.imdbId) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(MovieDetailsFetchingException::class.java) {
            runBlocking { gateway.getMovieDetailsByID(movieStorage.id) }
        }
        Assertions.assertEquals(
            "Error getting movie details form repository ${movieStorage.id}. Error detail [$errorMessage]",
            exception.message
        )
    }

    // TODO: this could be in a ObjectMother
    private val imbdResponse = IMDbResponse(
        title = "The Fast and the Furious",
        year = "2001",
        rated = "PG-13",
        released = "22 Jun 2001",
        runtime = "106 min",
        genre = "Action, Crime, Thriller",
        director = "Rob Cohen",
        writer = "Ken Li, Gary Scott Thompson, Erik Bergquist",
        actors = "Vin Diesel, Paul Walker, Michelle Rodriguez",
        plot = "Los Angeles police officer Brian O'Conner must decide where his loyalty really lies when he...",
        language = "English, Spanish",
        country = "United States, Germany",
        awards = "11 wins & 18 nominations",
        poster = "https://m.media-amazon.com/images/M/MV5BNzlkNzVjMD.jpg",
        ratings = emptyList(),
        metascore = "58",
        imdbRating = "6.8",
        imdbVotes = "370,116",
        imdbID = "tt0232500",
        type = "movie",
        dVD = "03 Jun 2003",
        boxOffice = "$144,533,925",
        production = "N/A",
        website = "N/A",
        response = "true"
    )
}
