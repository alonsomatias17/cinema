package com.cinema.adapters.infraestructure.httpClient

import com.cinema.adapters.infraestructure.httpClient.exceptions.DeserializationException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias MapperFlavor = ObjectMapper

class Mapper(val mapper: MapperFlavor = defaultCamelConfig()) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        fun defaultCamelCaseMapper() = Mapper(defaultCamelConfig())
        fun secondaryCamelCaseMapper() = Mapper(secondaryCamelConfig())
    }

    fun serialize(obj: Any): String {
        return mapper.writeValueAsString(obj)
    }

    inline fun <reified T> deserialize(bytes: ByteArray): T {
        return try {
            mapper.readValue(bytes)
        } catch (e: Exception) {
            val errorMessage = "Deserialization error. [Error detail: ${e.message}]"
            log.error(errorMessage)
            throw DeserializationException(errorMessage)
        }
    }

    inline fun <reified T> deserialize(value: String): T {
        return try {
            mapper.readValue(value)
        } catch (e: Exception) {
            throw DeserializationException("Deserialization error. [Error detail: ${e.message}]")
        }
    }

    fun simpleDeserialize(bytes: ByteArray): String {
        return String(bytes, Charsets.UTF_8)
    }
}

private fun defaultCamelConfig(): MapperFlavor {
    return ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE)
        .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())
}

private fun secondaryCamelConfig(): MapperFlavor {
    return ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
        .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())
}
