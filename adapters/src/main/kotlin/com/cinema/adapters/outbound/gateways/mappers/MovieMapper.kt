package com.cinema.adapters.outbound.gateways.mappers

import com.cinema.adapters.outbound.repositories.dto.MovieStorage
import com.cinema.adapters.outbound.repositories.dto.Schedule
import com.cinema.domain.models.Movie
import java.time.DayOfWeek
import com.cinema.domain.models.Schedule as DomainSchedule

// TODO: this should be tested

fun MovieStorage.toDomain(): Movie {
    return Movie(id = id, title = title, imdbId = imdbId, ticketPrice = ticketPrice, schedules = schedules.toDomain())
}

fun List<Schedule>.toDomain(): List<DomainSchedule> {
    return this.map { DomainSchedule(dayOfWeek = DayOfWeek.valueOf(it.dayOfWeek), times = it.times) }
}

fun Movie.toStorage(): MovieStorage {
    return MovieStorage(
        id = id,
        title = title,
        imdbId = imdbId,
        ticketPrice = ticketPrice,
        schedules = schedules.toStorage()
    )
}

fun List<DomainSchedule>.toStorage(): List<Schedule> {
    return this.map { Schedule(dayOfWeek = it.dayOfWeek.name, times = it.times) }
}
