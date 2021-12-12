package com.cinema.application.configuration

object Environment {

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
