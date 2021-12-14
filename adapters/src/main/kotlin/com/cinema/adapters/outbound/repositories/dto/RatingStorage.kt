package com.cinema.adapters.outbound.repositories.dto

import com.cinema.adapters.outbound.repositories.dto.RatingStorage.Companion.toKey
import com.cinema.adapters.outbound.repositories.dto.RatingStorage.Companion.toSortKey
import com.cinema.adapters.outbound.repositories.dto.RatingStorage.Companion.toUnKey
import com.cinema.adapters.outbound.repositories.dto.RatingStorage.Companion.toUnSortKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
data class RatingStorage(
    @get:DynamoDbPartitionKey @get:DynamoDbAttribute("PK") var movieId: String = "",
    @get:DynamoDbSortKey @get:DynamoDbAttribute("SK") var sortKey: String = "",
    var scores: List<ScoreStorage> = emptyList()
) {
    companion object {
        private const val TABLE_NAME = "Movies"
        private const val ENTITY = "Rating"
        private const val KEY_PREFIX = "mo#"
        private const val SORT_KEY_PREFIX = "ra#"

        fun toKey(key: String) = "$KEY_PREFIX$key"
        fun toSortKey(sortKey: String) = "$SORT_KEY_PREFIX$sortKey"
        fun toUnKey(key: String) = key.replace(KEY_PREFIX, "")
        fun toUnSortKey(sortKey: String) = sortKey.replace(SORT_KEY_PREFIX, "")
        fun tableName() = TABLE_NAME
        fun entityName() = ENTITY
    }
}

@DynamoDbBean
data class ScoreStorage(var userName: String = "", var score: Int = 0)

fun RatingStorage.unBuildKey() = this.copy(movieId = toUnKey(this.movieId), sortKey = toUnSortKey(this.sortKey))

fun RatingStorage.buildKey() = this.copy(movieId = toKey(this.movieId), sortKey = toSortKey(this.movieId))
