package com.cinema.adapters.outbound.repositories.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class IMDbResponse(
    val title: String,
    val year: String,
    val rated: String,
    val released: String,
    val runtime: String,
    val genre: String,
    val director: String,
    val writer: String,
    val actors: String,
    val plot: String,
    val language: String,
    val country: String,
    val awards: String,
    val poster: String,
    val ratings: List<RatingsResponse>,
    val metascore: String,
    @JsonProperty("imdbRating") val imdbRating: String,
    @JsonProperty("imdbVotes") val imdbVotes: String,
    @JsonProperty("imdbID") val imdbID: String,
    val type: String,
    val dVD: String,
    val boxOffice: String,
    val production: String,
    val website: String,
    val response: String
)

data class RatingsResponse(
    val source: String,
    val value: String
)
