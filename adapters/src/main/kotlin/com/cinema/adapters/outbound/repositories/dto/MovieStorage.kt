package com.cinema.adapters.outbound.repositories.dto

import com.cinema.adapters.outbound.repositories.dto.MovieStorage.Companion.toKey
import com.cinema.adapters.outbound.repositories.dto.MovieStorage.Companion.toSortKey
import com.cinema.adapters.outbound.repositories.dto.MovieStorage.Companion.toUnKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.LocalTime

@DynamoDbBean
data class MovieStorage(
    @get:DynamoDbPartitionKey @get:DynamoDbAttribute("PK") var id: String = "",
    @get:DynamoDbSortKey @get:DynamoDbAttribute("SK") var sortKey: String = "",
    var title: String = "",
    var ticketPrice: String = "",
    var schedules: List<Schedule> = emptyList()
) {
    companion object {
        private const val TABLE_NAME = "movies"
        private const val KEY_PREFIX = "mo#"
        private const val SORT_KEY_PREFIX = "mo#"

        fun toKey(key: String) = "$KEY_PREFIX$key"
        fun toSortKey(sortKey: String) = "$SORT_KEY_PREFIX$sortKey"
        fun toUnKey(key: String) = key.replace(KEY_PREFIX, "")
        fun toUnSortKey(sortKey: String) = sortKey.replace(SORT_KEY_PREFIX, "")
        fun tableName() = TABLE_NAME
    }
}

@DynamoDbBean
data class Schedule(var dayOfWeek: String = "", var startTime: LocalTime = LocalTime.MIN)

fun MovieStorage.unBuildKey() = this.copy(id = toUnKey(this.id))

fun MovieStorage.buildKey() = this.copy(id = toKey(this.id), sortKey = toSortKey(this.id))
