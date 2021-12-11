package com.cinema.application.configuration

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object Config {

    private val stores = getStores()

    fun get(): Config {
        return stores
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(path: String): T {
        return stores.getAnyRef(path) as T
    }

    private fun getStores(): Config {
        return ConfigFactory.parseResources("application.${Environment.getEnvironment()}.conf")
            .withFallback(ConfigFactory.parseMap(mapOf<String, Any>()).resolve())
            .resolve()
    }
}
