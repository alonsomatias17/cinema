package com.cinema.adapters.infraestructure.database

import java.time.Duration

data class DBConfig(
    val accessKey: String,
    val secretKey: String,
    val region: String,
    val endpoint: String,
    val maxConcurrency: Int,
    val connectionTimeout: Duration,
    val requestReadTimeout: Duration,
    val maxPendingConnectionAcquires: Int,
    val retries: Int
)
