package com.cinema.application.modules

import com.cinema.adapters.inbound.handlers.MovieHandler
import com.cinema.adapters.infraestructure.database.DBClient
import com.cinema.adapters.infraestructure.database.DBConfig
import com.cinema.adapters.infraestructure.database.DynamoClientFactory
import com.cinema.adapters.outbound.gateways.MovieGateway
import com.cinema.adapters.outbound.repositories.IMovieRepository
import com.cinema.adapters.outbound.repositories.MovieRepository
import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import com.cinema.application.configuration.Config
import com.cinema.domain.ports.inbound.IGetMovieByIDPort
import com.cinema.domain.ports.outbound.IGetMoviePort
import com.cinema.domain.usecases.GetMovieByIdUseCase
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.time.Duration

object ModuleLoader {

    val modules = module(createdAtStart = true) {
        injectDatabase()
        single { MovieRepository(get()) } bind IMovieRepository::class
        single { MovieGateway(get()) } bind IGetMoviePort::class
        single { GetMovieByIdUseCase(get()) } bind IGetMovieByIDPort::class
        single { MovieHandler(get()) }
    }

    fun Module.injectDatabase() {
        val dynamoClient = DynamoClientFactory.createEnhancedAsyncClient(getDbConfig())
        val tables: Map<String, DynamoDbAsyncTable<*>> = getTables(dynamoClient)
        single { DBClient(dynamoClient, tables) }
    }

    private fun getTables(dynamoClient: DynamoDbEnhancedAsyncClient) =
        mapOf(
            MovieStorage.tableName() to dynamoClient.table(
                MovieStorage.tableName(),
                TableSchema.fromClass(MovieStorage::class.java)
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
