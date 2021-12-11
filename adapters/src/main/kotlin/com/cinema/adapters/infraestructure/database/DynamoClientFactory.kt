package com.cinema.adapters.infraestructure.database

import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

object DynamoClientFactory {

    fun createEnhancedAsyncClient(dbConfig: DBConfig): DynamoDbEnhancedAsyncClient =
        DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(createDynamoAsyncClient(dbConfig)).build()

    private fun createDynamoAsyncClient(dbConfig: DBConfig): DynamoDbAsyncClient {
        return try {
            val dynamoBuilder = DynamoDbAsyncClient.builder()
                .overrideConfiguration { it.retryPolicy { policy -> policy.numRetries(dbConfig.retries) } }
                .region(Region.of(dbConfig.region))
                .credentialsProvider(buildCredentialsProvider(dbConfig))
                .httpClientBuilder(buildHttpClient(dbConfig))

            if (dbConfig.endpoint.isNotBlank()) {
                dynamoBuilder.endpointOverride(URI(dbConfig.endpoint))
            }
            dynamoBuilder.build()
        } catch (ex: Exception) {
            LoggerFactory.getLogger(this::class.java)
                .error("Error when try connect to dynamo DB. Error details [${ex.message}]")
            throw ex
        }
    }

    private fun buildCredentialsProvider(dbConfig: DBConfig) =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(dbConfig.accessKey, dbConfig.secretKey))

    private fun buildHttpClient(dbConfig: DBConfig) = NettyNioAsyncHttpClient
        .builder()
        .maxConcurrency(dbConfig.maxConcurrency)
        .connectionTimeout(dbConfig.connectionTimeout)
        .readTimeout(dbConfig.requestReadTimeout)
        .connectionAcquisitionTimeout(dbConfig.connectionTimeout)
        .maxPendingConnectionAcquires(dbConfig.maxPendingConnectionAcquires)
}
