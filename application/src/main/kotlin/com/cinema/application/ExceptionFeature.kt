package com.cinema.application

import com.cinema.application.metrics.Metrics.noticeError
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.BadRequestException
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.exceptions() {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    val messageKey = "message"
    val codeKey = "code"

    install(StatusPages) {
        exception<BadRequestException> {
            error(it, HttpStatusCode.BadRequest)
        }
/*        exception<BaseException> {
            error(it)
        }*/

        // unhandled errors
        exception<Exception> {
            log.error("internal error - not mapped exception", it)
            noticeError(it)
            call.respond(
                HttpStatusCode.InternalServerError,
                hashMapOf(messageKey to it.message, codeKey to "unhandled.internal.exception")
            )
        }
        exception<Error> {
            log.error("internal error - not mapped exception", it)
            noticeError(it)
            call.respond(
                HttpStatusCode.InternalServerError,
                hashMapOf(messageKey to it.message, codeKey to "unhandled.internal.error")
            )
        }
    }
}
private suspend fun PipelineContext<Unit, ApplicationCall>.error(
    it: Exception,
    statusCode: HttpStatusCode = HttpStatusCode.InternalServerError,
) {
    noticeError(it)
    call.respond(statusCode, it)
}
// TODO: uncomment
/*private suspend fun PipelineContext<Unit, ApplicationCall>.error(
    it: BaseException,
    statusCode: HttpStatusCode = HttpStatusCode.InternalServerError,
) {
    noticeError(it)
    call.respond(statusCode, it.getMessageMap())
}*/
