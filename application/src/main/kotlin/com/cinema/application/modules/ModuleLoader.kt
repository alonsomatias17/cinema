package com.cinema.application.modules

import org.koin.core.module.Module
import org.koin.dsl.module

internal const val CLIENT_NAME = "cart"
internal const val HTTP_CLIENT = "cart_http_client"

object ModuleLoader {

    val modules = module(createdAtStart = true) {
        metricsDependencies()

//        getCartServicesClient()
//        single { CartServicesRepositoryImpl(get()) } bind CartServicesRepository::class
//        single { CreateCartUseCase(get(), get()) }
//        single { CreateCartHandler(get()) }
    }

    private fun Module.metricsDependencies() {
//        single { KtorMicrometer.getRegistry() }
    }

    private fun Module.getSingleHttpClient() {
//        single(named(nameHttpClient)) { HttpClientFactory.create(httpClientConfig) }
    }
}
