package com.cinema.adapters.outbound.repositories

import com.cinema.adapters.outbound.clients.IMDbClient
import com.cinema.adapters.outbound.repositories.dto.IMDbResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MovieDetailsRepositoryTest {

    private val imdbClient: IMDbClient = mockk()
    private val repository = MovieDetailsRepository(imdbClient)

    @Test
    fun `given a imdb id a IMDbResponse is returned`() {
        val imdbID = "tt12345"

        coEvery { imdbClient.getIMDbDetails(imdbID) } returns expectedResponse

        val result = runBlocking { repository.getMovieDetailsByImdbID(imdbID) }

        Assertions.assertEquals(expectedResponse, result)
    }

    @Test
    fun `given an error getting movie details a Exception is thrown`() {
        val imdbID = "tt12345"
        val errorMessage = "Error getting movie details"

        coEvery { imdbClient.getIMDbDetails(imdbID) } throws Exception(errorMessage)

        val exception = Assertions.assertThrows(Exception::class.java) {
            runBlocking { repository.getMovieDetailsByImdbID(imdbID) }
        }

        Assertions.assertEquals(
            "Error getting movie details for imdbID: $imdbID. Error detail [$errorMessage]",
            exception.message
        )
    }

    private val expectedResponse = IMDbResponse(
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
