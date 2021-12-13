package com.cinema.adapters.outbound.gateways.mappers

import com.cinema.adapters.outbound.repositories.dto.IMDbResponse
import com.cinema.adapters.outbound.repositories.dto.RatingsResponse
import com.cinema.domain.models.MovieDetails
import com.cinema.domain.models.Ratings

fun IMDbResponse.toDomain(): MovieDetails {
    return MovieDetails(
        title = title,
        year = year,
        rated = rated,
        released = released,
        runtime = runtime,
        genre = genre,
        director = director,
        writer = writer,
        actors = actors,
        plot = plot,
        language = language,
        country = country,
        awards = awards,
        poster = poster,
        ratings = ratings.toDomain(),
        metascore = metascore,
        imdbRating = imdbRating,
        imdbVotes = imdbVotes,
        imdbID = imdbID,
        type = type,
        dVD = dVD,
        boxOffice = boxOffice,
        production = production,
        website = website,
        response = response
    )
}

private fun List<RatingsResponse>.toDomain(): List<Ratings> {
    return this.map { Ratings(it.source, it.value) }
}
