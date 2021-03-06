package com.cinema.application.modules

import com.cinema.adapters.inbound.handlers.MovieHandler
import com.cinema.adapters.infraestructure.database.DBClient
import com.cinema.adapters.infraestructure.database.DBConfig
import com.cinema.adapters.infraestructure.database.DynamoClientFactory
import com.cinema.adapters.infraestructure.httpClient.Client
import com.cinema.adapters.outbound.clients.IMDbClient
import com.cinema.adapters.outbound.gateways.MovieDetailsGateway
import com.cinema.adapters.outbound.gateways.MovieGateway
import com.cinema.adapters.outbound.gateways.MovieRatingGateway
import com.cinema.adapters.outbound.repositories.IMovieDetailsRepository
import com.cinema.adapters.outbound.repositories.IMovieRepository
import com.cinema.adapters.outbound.repositories.IRatingRepository
import com.cinema.adapters.outbound.repositories.MovieDetailsRepository
import com.cinema.adapters.outbound.repositories.MovieRepository
import com.cinema.adapters.outbound.repositories.RatingRepository
import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import com.cinema.adapters.outbound.repositories.dto.RatingStorage
import com.cinema.application.configuration.Config
import com.cinema.domain.ports.inbound.IGetMovieByIDPort
import com.cinema.domain.ports.inbound.IGetMovieDetailsByIDPort
import com.cinema.domain.ports.inbound.IRateMoviePort
import com.cinema.domain.ports.outbound.IAddMovieRatingPort
import com.cinema.domain.ports.outbound.IGetMovieDetailsPort
import com.cinema.domain.ports.outbound.IGetMoviePort
import com.cinema.domain.usecases.GetMovieByIdUseCase
import com.cinema.domain.usecases.GetMovieDetailsByIDUseCase
import com.cinema.domain.usecases.RateMovieUseCase
import com.cinema.domain.usecases.UpdateMovieUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.impl.nio.reactor.IOReactorConfig
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.time.Duration
import com.cinema.domain.ports.inbound.IUpdateMoviePort as IUpdateMovieInboundPort
import com.cinema.domain.ports.outbound.IUpdateMoviePort as IUpdateMovieOutboundPort

internal const val IMDB_HTTP_CLIENT = "imbdClient_http_client"
internal const val IMDB_KEY_ENV = "imdbKey"

object ModuleLoader {

    val modules = module(createdAtStart = true) {
        injectDatabase()
        single(named(IMDB_HTTP_CLIENT)) { createClient() }
        // TODO: host should be in a conf file
        // TODO: IMDB_KEY_ENV should be in VAULT
        single {
            IMDbClient(
                secret = System.getenv(IMDB_KEY_ENV),
                host = "http://www.omdbapi.com",
                client = get(named(IMDB_HTTP_CLIENT))
            )
        }
        single { MovieDetailsRepository(get()) } bind IMovieDetailsRepository::class
        single { MovieRepository(get()) } bind IMovieRepository::class
        single { RatingRepository(get()) } bind IRatingRepository::class

        single { MovieDetailsGateway(get(), get()) } bind IGetMovieDetailsPort::class
        single { MovieGateway(get()) } binds arrayOf(IGetMoviePort::class, IUpdateMovieOutboundPort::class)
        single { MovieRatingGateway(get()) } bind IAddMovieRatingPort::class

        single { GetMovieDetailsByIDUseCase(get()) } bind IGetMovieDetailsByIDPort::class
        single { GetMovieByIdUseCase(get()) } bind IGetMovieByIDPort::class
        single { UpdateMovieUseCase(get()) } bind IUpdateMovieInboundPort::class
        single { RateMovieUseCase(get()) } bind IRateMoviePort::class

        single { MovieHandler(get(), get(), get(), get()) }
    }

    fun Module.injectDatabase() {
        val dynamoClient = DynamoClientFactory.createEnhancedAsyncClient(getDbConfig())
        val tables: Map<String, DynamoDbAsyncTable<*>> = getTables(dynamoClient)
        single { DBClient(dynamoClient, tables) }
    }

    private fun getTables(dynamoClient: DynamoDbEnhancedAsyncClient) = mapOf(
        MovieStorage.entityName() to dynamoClient.table(
            MovieStorage.tableName(),
            TableSchema.fromClass(MovieStorage::class.java)
        ),
        RatingStorage.entityName() to dynamoClient.table(
            RatingStorage.tableName(),
            TableSchema.fromClass(RatingStorage::class.java)
        )
    )
}

fun getDbConfig(): DBConfig {
    return DBConfig(
        accessKey = Config.get("aws.awsAccess"),
        secretKey = Config.get("aws.awsSecret"),
        region = Config.get("aws.region"),
        endpoint = Config.get("aws.endpoint"),
        maxConcurrency = Config.get("aws.dynamo.maxConcurrency"),
        connectionTimeout = Duration.ofMillis(Config.get("aws.dynamo.connectionTimeout")),
        requestReadTimeout = Duration.ofMillis(Config.get("aws.dynamo.requestReadTimeout")),
        maxPendingConnectionAcquires = Config.get("aws.dynamo.maxPendingConnections"),
        retries = Config.get("aws.dynamo.retries")
    )
}

// TODO: config values should be in a conf file
fun createClient(): Client {
    val cfg = IOReactorConfig.custom()
        .setIoThreadCount(10)
        .setSelectInterval(400)
        .build()

    val manager = PoolingNHttpClientConnectionManager(DefaultConnectingIOReactor(cfg))
    manager.defaultMaxPerRoute = 10
    manager.maxTotal = 10 * 2

    return HttpClient(Apache) {

        expectSuccess = false

        engine {

            customizeClient {
                setMaxConnTotal(10)
                setMaxConnPerRoute(10)
                setConnectionManager(manager)
            }

            socketTimeout = 3000
            connectTimeout = 3000 * 2
            connectionRequestTimeout = 3000
        }
    }
}
