package com.cinema.application.configuration

import io.ktor.config.ApplicationConfig
import io.ktor.config.ApplicationConfigValue
import io.ktor.config.ApplicationConfigurationException
import io.ktor.config.HoconApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import com.typesafe.config.Config as ConfigRetriever

@OptIn(KtorExperimentalAPI::class)
class ServerConfig(private val config: Config) : ApplicationConfig {

    private val retriever = Config.get()

    override fun property(path: String): ApplicationConfigValue {
        if (!retriever.hasPath(path)) {
            throw ApplicationConfigurationException("Property $path not found.")
        }
        return ConfigValue(retriever, path)
    }

    override fun propertyOrNull(path: String): ApplicationConfigValue? {
        if (!retriever.hasPath(path)) {
            return null
        }
        return ConfigValue(retriever, path)
    }

    override fun configList(path: String): List<ApplicationConfig> {
        return retriever.getConfigList(path).map { HoconApplicationConfig(it) }
    }

    override fun config(path: String): ApplicationConfig = ServerConfig(config)

    private class ConfigValue(val config: ConfigRetriever, val path: String) : ApplicationConfigValue {
        override fun getString(): String = config.getString(path)
        override fun getList(): List<String> = config.getStringList(path)
    }
}
