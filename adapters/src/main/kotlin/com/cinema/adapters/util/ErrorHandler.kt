package com.cinema.adapters.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("ErrorHandler")

fun Result<*>.handleFailure(
    baseErrorMessage: String,
    className: String? = null,
    exceptionThrown: ((String) -> Nothing)? = null,
) {
    if (this.isFailure) {
        val errorMessage = "$baseErrorMessage. Error detail [${this.exceptionOrNull()?.message}]"
        className?.let { log.atError().addKeyValue("Class", className).log(errorMessage) } ?: log.atError()
            .log(errorMessage)
        exceptionThrown?.apply { this.invoke(errorMessage) }
    }
}
