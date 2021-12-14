package com.cinema.adapters.inbound.handlers.request

data class RatingRequest(val movieID: String, val userName: String, val score: Int)
