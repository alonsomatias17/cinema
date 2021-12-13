package com.cinema.adapters.infraestructure.httpClient

import com.cinema.adapters.infraestructure.httpClient.exceptions.HttpException
import com.cinema.adapters.infraestructure.httpClient.exceptions.HttpExternalException
import com.cinema.adapters.infraestructure.httpClient.exceptions.HttpInternalException
import com.cinema.adapters.infraestructure.httpClient.exceptions.HttpNotFoundException
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.apache.http.HttpStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias Client = HttpClient
typealias Response = HttpResponse

abstract class AbstractClient(
    open val host: String,
    open val client: Client,
    open val mapper: Mapper = Mapper.defaultCamelCaseMapper()
) {

    abstract val clientName: String
    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        val status2xx = 200..299
        val status4xx = 400..499
        val status5xx = 500..599
        val errorStatus = 400..599
    }

    protected suspend inline fun <reified T> get(
        uri: String,
        params: Map<String, Any> = mapOf(),
        headers: Map<String, Any> = mapOf()
    ): T {
        val getClientOperation: suspend (HttpRequestBuilder) -> Response =
            { requestBuilder -> client.get(requestBuilder) }
        return exchange(HttpMethod.Get, getClientOperation, uri, params, headers)
    }

    protected suspend inline fun <reified T> exchange(
        method: HttpMethod,
        crossinline clientOperation: suspend (HttpRequestBuilder) -> Response,
        uri: String,
        params: Map<String, Any> = mapOf(),
        headers: Map<String, Any> = mapOf(),
        body: Any? = null
    ): T {
        return try {
            val requestBuilder = request(method = method, uri = uri, params = params, body = body, headers = headers)

            val response: HttpResponse = withResilience { clientOperation(requestBuilder) }
            val bytes = response.receive<ByteArray>()
            this.handleStatus(bytes, response.status.value)

            log.debug("Request correctly executed: [$requestBuilder]")

            mapper.deserialize(bytes)
        } catch (e: HttpException) {
            log.error("Mapped error calling $uri. Error message ${e.message}")
            throw e
        } catch (e: Exception) {
            log.error("Unmapped error calling $uri. Error message ${e.message}")
            throw HttpInternalException("Error calling $uri. Error detail [${e.message}]")
        }
    }

    protected fun request(
        method: HttpMethod,
        uri: String,
        params: Map<String, Any>,
        body: Any? = null,
        headers: Map<String, Any>
    ): HttpRequestBuilder {
        val requestBuilder = HttpRequestBuilder()
        requestBuilder.url("$host$uri")
        requestBuilder.method = method
        if (body != null) {
            requestBuilder.body = mapper.serialize(body)
        }

        params.entries.forEach { entry -> requestBuilder.parameter(entry.key, entry.value.toString()) }
        headers.entries.forEach { entry -> requestBuilder.header(entry.key, entry.value.toString()) }

        return requestBuilder
    }

    protected suspend fun <T> withResilience(
        block: suspend () -> T
    ): T {
        // TODO: add some resilience decorators (e.g. retries, circuit breakers, to; e.g. resilience4j)
        return block()
    }

    protected open fun handleStatus(body: ByteArray, statusCode: Int) {
        when (statusCode) {
            in status2xx -> return
            in errorStatus -> handleError(body, statusCode)
            else -> throw HttpExternalException(String(body, Charsets.UTF_8))
        }
    }

    private fun handleError(body: ByteArray, statusCode: Int) {
        val convertedBody = mapper.simpleDeserialize(body)
        val errorMessage = "Unexpected http response from $clientName client. Status code: $statusCode. " +
                "Error detail: [$convertedBody]"
        log.error(errorMessage)
        when (statusCode) {
            HttpStatus.SC_NOT_FOUND -> throw HttpNotFoundException(convertedBody)
            in status4xx -> throw HttpInternalException(convertedBody)
            in status5xx -> throw HttpExternalException(convertedBody)
        }
    }
}
