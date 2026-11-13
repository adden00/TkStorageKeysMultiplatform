package com.adden00.tkstoragekeys.di

import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.data.auth.AuthRepository
import com.adden00.tkstoragekeys.data.auth.SessionManager
import com.adden00.tkstoragekeys.data.local.AppSettings
import com.adden00.tkstoragekeys.data.network.BackendApiService
import com.adden00.tkstoragekeys.data.network.StorageApi
import com.adden00.tkstoragekeys.features.add_equip_screen.NewEquipViewModel
import com.adden00.tkstoragekeys.features.enter_password_screen.EnterPasswordViewModel
import com.adden00.tkstoragekeys.features.global_history_screen.GlobalHistoryViewModel
import com.adden00.tkstoragekeys.features.item_history_screen.ItemHistoryViewModel
import com.adden00.tkstoragekeys.features.location_cleanup.LocationCleanupViewModel
import com.adden00.tkstoragekeys.features.people_search_screen.PeopleSearchViewModel
import com.adden00.tkstoragekeys.features.person_details_screen.PersonDetailsViewModel
import com.adden00.tkstoragekeys.features.reception_screen.ReceptionViewModel
import com.adden00.tkstoragekeys.features.users_search.UsersSearchViewModel
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private fun dataModule() = module {

    factory<HttpClient> {
        HttpClient {
            install(Logging) {
                level = LogLevel.INFO
                sanitizeHeader { it == HttpHeaders.Authorization }
                logger = object : Logger {
                    override fun log(message: String) {
                        // Custom log logic, for example, log to a file
                        println("Custom Log: $message")
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    factory<StorageApi> {
        val appSettings = get<AppSettings>()
        val sessionManager = get<SessionManager>()
        BackendApiService(
            api = get(),
            isTestEnv = { appSettings.isTestEnv },
            tokenProvider = { sessionManager.token },
            onUnauthorized = sessionManager::onUnauthorized,
        )
    }

    single { SessionManager(get()) }

    factory { AuthRepository(api = get(), appSettings = get()) }

    factory<AppSettings> {
        AppSettings(
            settings = Settings()
        )
    }

    factory<StorageRepository> {
        StorageRepository(
            api = get<StorageApi>(),
        )
    }

}

fun viewModelModule() = module {
    viewModel {
        LocationCleanupViewModel()
    }
    viewModel {
        ReceptionViewModel()
    }

    viewModel {
        NewEquipViewModel()
    }

    viewModel {
        PeopleSearchViewModel()
    }

    viewModel {
        ItemHistoryViewModel()
    }

    viewModel {
        GlobalHistoryViewModel()
    }

    viewModel {
        UsersSearchViewModel()
    }

    viewModel {
        PersonDetailsViewModel()
    }

    viewModel {
        EnterPasswordViewModel()
    }
}

fun getCommonModules() = listOf(dataModule(), viewModelModule())

fun initKoin() {
    startKoin {
        modules(getCommonModules())
    }
}
