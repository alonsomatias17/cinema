package com.cinema.adapters.infraestructure.httpClient.exceptions

abstract class HttpException(override val message: String) : RuntimeException(message)
