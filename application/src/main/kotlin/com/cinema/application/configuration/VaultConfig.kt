package com.cinema.application.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class VaultConfig {

    companion object {
        private const val VAULT_HOST: String = "vault.peya.co"
        private const val VAULT_VERSION: String = "v1"
        private const val VAULT_HEADER_KEY: String = "X-Vault-Token"
        private const val VAULT_URI: String = "secret/cinema"
        private const val BASE_PATH: String = "HOME"
        private const val TOKEN_FILE_NAME: String = "/.vault_token"
    }

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun getSecrets(): Map<String, Any> {
        return runBlocking {
            val client = getClient()
            return@runBlocking try {
                val token = getVaultToken()
                val env = Environment.getEnvironment()
                val url = "https://$VAULT_HOST/$VAULT_VERSION/$VAULT_URI/$env"
                client.get<Vault>(url) {
                    header(VAULT_HEADER_KEY, token)
                }.data
            } catch (ex: ClientRequestException) {
                log.warn("Error executing request. Error detail: [${ex.message}]")
                emptyMap()
            } finally {
                client.close()
            }
        }
    }

    private fun getVaultToken(): String {
        return if (Environment.isDevelopment()) getLocalVaultToken() else Environment.getVaultToken()
    }

    private fun getLocalVaultToken(): String {
        val path = "${System.getenv(BASE_PATH)}$TOKEN_FILE_NAME"
        return File(path).readText(Charsets.UTF_8).trim()
    }

    private fun getClient(): HttpClient {
        return HttpClient(Apache) {
            install(JsonFeature) {
                serializer = JacksonSerializer {
                    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                }
            }
        }
    }

    private data class Vault(
        val data: Map<String, Any>
    )
}
