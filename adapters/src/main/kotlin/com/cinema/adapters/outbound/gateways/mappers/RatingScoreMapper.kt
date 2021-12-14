package com.cinema.adapters.outbound.gateways.mappers

import com.cinema.adapters.outbound.repositories.dto.ScoreStorage
import com.cinema.domain.models.RatingScore

fun RatingScore.toStorage() = ScoreStorage(userName, score)
