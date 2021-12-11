package com.cinema.application.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Environment {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private const val VAULT_TOKEN_ENV = "VAULT_TOKEN"
    private const val ENV_VARIABLE = "ENV"
    private const val DEVELOPMENT = "dev"
    private const val STAGING = "stg"
    private const val PRODUCTION = "live"
    private const val TEST = "test"

    fun isDevelopment(): Boolean {
        return DEVELOPMENT.contains(getEnvironment())
    }

    fun isTest(): Boolean {
        return TEST.contains(getEnvironment())
    }

    fun isStaging(): Boolean {
        return STAGING.contains(getEnvironment())
    }

    fun isProduction(): Boolean {
        return PRODUCTION.contains(getEnvironment())
    }

    fun getVaultToken(): String {
        return getVariable(VAULT_TOKEN_ENV)
    }

    fun getEnvironment(): String {
        return getVariable(ENV_VARIABLE, DEVELOPMENT)
    }

    private fun getVariable(variable: String, default: String = ""): String {
        val property = System.getProperty(variable)
        if (property.isNullOrEmpty()) {
            val envVariable = System.getenv(variable)
            if (envVariable.isNullOrEmpty()) {
                System.setProperty(variable, default)
            } else {
                System.setProperty(variable, envVariable)
            }
        }
        return System.getProperty(variable)
    }
}
