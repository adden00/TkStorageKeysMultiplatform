package com.adden00.tkstoragekeys.data.auth

import com.adden00.tkstoragekeys.data.EquipNotFoundException
import com.adden00.tkstoragekeys.data.local.AppSettings
import com.adden00.tkstoragekeys.data.network.StorageApi
import kotlinx.coroutines.CancellationException

data class EndedSession(val token: String, val isTestEnv: Boolean)

class AuthRepository(
    private val api: StorageApi,
    private val appSettings: AppSettings,
) {

    val isLoggedIn: Boolean get() = appSettings.authToken.isNotEmpty()

    suspend fun login(keyholderName: String, key: String) {
        val previous = currentSession()
        appSettings.isTestEnv = key.trim() == TEST_KEY
        val response = try {
            api.login(keyholderName.trim(), key.trim())
        } catch (e: Exception) {
            appSettings.isTestEnv = previous?.isTestEnv ?: false
            throw e
        }
        if (!response.success || response.token.isNullOrEmpty()) {
            appSettings.isTestEnv = previous?.isTestEnv ?: false
            throw EquipNotFoundException(response.message ?: "Ключ неверный")
        }
        appSettings.authToken = response.token
        previous?.let { revoke(it) }
    }

    fun endSession(): EndedSession? {
        val session = currentSession()
        appSettings.clearSession()
        return session
    }

    suspend fun revoke(session: EndedSession) {
        try {
            api.logout(session.token, session.isTestEnv)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        }
    }

    private fun currentSession(): EndedSession? =
        appSettings.authToken.takeIf { it.isNotEmpty() }?.let { EndedSession(it, appSettings.isTestEnv) }

    private companion object {
        const val TEST_KEY = "testenv"
    }
}
