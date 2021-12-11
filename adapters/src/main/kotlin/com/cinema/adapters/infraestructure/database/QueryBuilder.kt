package com.cinema.adapters.infraestructure.database

import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

class QueryBuilder {

    private var key: Key? = null
    private var filter: QueryConditional? = null

    /**
     * This method builds a Key, it should be used when expecting to receive one item in return
     * @param partitionKey A partitionKey type String to partially determine the unique key
     * @param sortKey A sortKey type Long to partially determine the unique key
     */
    fun buildWithKey(partitionKey: String, sortKey: Long): QueryBuilder {
        key = Key.builder().partitionValue(partitionKey).sortValue(sortKey).build()
        return this
    }

    /**
     * This method builds a Key, it should be used when expecting to receive one item in return
     * @param partitionKey A partitionKey type String to partially determine the unique key
     * @param sortKey A sortKey type String to partially determine the unique key
     */
    fun buildWithKey(partitionKey: String, sortKey: String): QueryBuilder {
        key = Key.builder().partitionValue(partitionKey).sortValue(sortKey).build()
        return this
    }

    /**
     * This method builds a Key, it should be used when expecting to receive one item in return
     * @param partitionKey A partitionKey type String to determine the unique key
     */
    fun buildWithKey(partitionKey: String): QueryBuilder {
        key = Key.builder().partitionValue(partitionKey).build()
        return this
    }

    fun getKey(): Key? {
        return key
    }

    fun buildFiltersWithKey(partitionKey: String): QueryBuilder {
        filter = QueryConditional.keyEqualTo { k -> k.partitionValue(partitionKey) }
        return this
    }

    fun getFilter(): QueryConditional? {
        return filter
    }
}
