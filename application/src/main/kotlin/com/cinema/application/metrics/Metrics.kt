package com.cinema.application.metrics

object Metrics {

    fun noticeError(message: String) {
        // TODO: some apm impl for notifications (e.g NewRelic or DataDog)
    }

    fun noticeError(ex: Exception) {
        // TODO: some apm impl for notifications (e.g NewRelic or DataDog)
    }

    fun noticeError(ex: Throwable) {
        // TODO: some apm impl for notifications (e.g NewRelic or DataDog)
    }

    fun noticeError(ex: Error) {
        // TODO: some apm impl for notifications (e.g NewRelic or DataDog)
    }
}
