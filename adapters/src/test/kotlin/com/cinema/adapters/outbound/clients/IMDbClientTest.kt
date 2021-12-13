package com.cinema.adapters.outbound.clients

import com.cinema.adapters.infraestructure.httpClient.Mapper
import com.cinema.adapters.infraestructure.httpClient.exceptions.HttpExternalException
import com.cinema.adapters.outbound.repositories.dto.IMDbResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IMDbClientTest {

    private lateinit var engine: MockEngine
    private lateinit var imdbClient: IMDbClient

    @BeforeAll
    fun setup() {
        engine = MockEngine(config = MockEngineConfig().apply { addHandler { error("Unhandled error") } })
        imdbClient = IMDbClient("localhost", HttpClient(engine) { expectSuccess = false })
    }

    @BeforeEach
    fun clean() {
        engine.config.requestHandlers.clear()
        (engine.responseHistory as MutableList).clear()
        (engine.requestHistory as MutableList).clear()
    }

    @Test
    fun `given a imdb id a IMDbResponse is returned`() {
        val expectedResponse = getIMDBResponseMock()
        engine.config.addHandler { respondOk(expectedResponse) }

        val response = runBlocking { imdbClient.getIMDbDetails("tt0232500") }

        Assertions.assertEquals(
            response,
            Mapper.defaultCamelCaseMapper().deserialize<IMDbResponse>(expectedResponse)
        )
    }

    @Test
    fun `getShopInfoByPartner throws HttpExternalException for status different from 4xx and 5xx`() {
        val errorMessage = "Error unexpected status"
        engine.config.addHandler { respond(errorMessage, status = HttpStatusCode.MultipleChoices) }

        val exception = Assertions.assertThrows(HttpExternalException::class.java) {
            runBlocking { imdbClient.getIMDbDetails("tt0232500") }
        }
        Assertions.assertEquals(errorMessage, exception.message)
    }

    private fun getIMDBResponseMock() =
        this.javaClass.getResource("/mocks/imdb-details-response.json")!!.readText()
}
