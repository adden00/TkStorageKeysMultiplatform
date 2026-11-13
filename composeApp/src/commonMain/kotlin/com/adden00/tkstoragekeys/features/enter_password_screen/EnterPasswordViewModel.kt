package com.adden00.tkstoragekeys.features.enter_password_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.EquipNotFoundException
import com.adden00.tkstoragekeys.data.auth.AuthRepository
import com.adden00.tkstoragekeys.data.local.AppSettings
import com.adden00.tkstoragekeys.features.enter_password_screen.mvi.EnterPasswordScreenEffect
import com.adden00.tkstoragekeys.features.enter_password_screen.mvi.EnterPasswordScreenEvent
import com.adden00.tkstoragekeys.features.enter_password_screen.mvi.EnterPasswordScreenState
import com.adden00.tkstoragekeys.utils.Platform
import com.adden00.tkstoragekeys.utils.getPlatform
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class EnterPasswordViewModel : ViewModel(), KoinComponent {

    private val authRepository: AuthRepository = get()
    private val appSettings: AppSettings = get()

    private val _viewState = MutableStateFlow(EnterPasswordScreenState())
    val viewState: StateFlow<EnterPasswordScreenState> = _viewState.asStateFlow()

    private val _viewEffect = Channel<EnterPasswordScreenEffect>()
    val viewEffect: Flow<EnterPasswordScreenEffect> = _viewEffect.receiveAsFlow()

    fun obtainEvent(event: EnterPasswordScreenEvent) {
        when (event) {
            EnterPasswordScreenEvent.CheckSession -> {
                appSettings.dropLegacyInventorySession()
                if (authRepository.isLoggedIn && getPlatform() != Platform.WEB) {
                    viewModelScope.launch { _viewEffect.send(EnterPasswordScreenEffect.OpenMain) }
                }
            }

            is EnterPasswordScreenEvent.Login -> {
                if (_viewState.value.isLoading || event.keyholderName.isBlank() || event.key.isBlank()) return
                _viewState.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    val effect = try {
                        authRepository.login(event.keyholderName, event.key)
                        EnterPasswordScreenEffect.OpenMain
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: EquipNotFoundException) {
                        EnterPasswordScreenEffect.ShowToast(e.message ?: "Ключ неверный")
                    } catch (e: Exception) {
                        EnterPasswordScreenEffect.ShowToast("Нет связи с сервером: ${e.message ?: ""}")
                    }
                    _viewState.update { it.copy(isLoading = false) }
                    _viewEffect.send(effect)
                }
            }
        }
    }
}
