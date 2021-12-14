package com.cinema.adapters.infraestructure.database

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.withContext
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient

class DBClient(
    val dynamoClient: DynamoDbEnhancedAsyncClient,
    val tablesMap: Map<String, DynamoDbAsyncTable<*>>,
) {

    inline fun <reified T> getTableByName(table: String): DynamoDbAsyncTable<T> {
      return tablesMap[table] as DynamoDbAsyncTable<T>
    }

    suspend inline fun <reified T> saveAsync(table: String, requestToSave: T): Deferred<Void> {
        return withContext(Dispatchers.IO) {
            getTableByName<T>(table).putItem(requestToSave).asDeferred()
        }
    }

    suspend inline fun <reified T> updateAsync(table: String, requestToSave: T): Deferred<T> {
        return withContext(Dispatchers.IO) {
            getTableByName<T>(table).updateItem(requestToSave).asDeferred()
        }
    }

    suspend inline fun <reified T> getAsync(table: String, queryBuilder: QueryBuilder): Deferred<T> {
        return withContext(Dispatchers.IO) {
            getTableByName<T>(table).getItem(queryBuilder.getKey()).asDeferred()
        }
    }
}
