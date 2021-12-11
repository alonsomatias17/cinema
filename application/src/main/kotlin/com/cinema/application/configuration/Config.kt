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
        val vaultConfig = ConfigFactory.parseMap(VaultConfig().getSecrets()).resolve()
        return ConfigFactory.parseResources("application.${Environment.getEnvironment()}.conf")
            .withFallback(vaultConfig)
            .resolve()
    }

    fun getAppName(): String {
        return System.getenv("DD_SERVICE") ?: "cinema"
    }

    fun getAppVersion(): String {
        return System.getenv("DD_VERSION") ?: "LOCAL_VERSION"
    }
}
