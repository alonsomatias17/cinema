package com.cinema.domain.models

import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalTime

data class Movie(
    val id: String,
    val title: String,
    val imdbId: String,
    val ticketPrice: BigDecimal,
    val schedules: List<Schedule>
)

data class Schedule(val dayOfWeek: DayOfWeek, val times: List<String>)
