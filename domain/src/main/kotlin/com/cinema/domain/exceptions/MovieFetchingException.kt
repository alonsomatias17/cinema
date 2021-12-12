package com.cinema.domain.exceptions

class MovieFetchingException(override val message: String) : RuntimeException(message)
