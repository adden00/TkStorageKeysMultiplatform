package com.adden00.tkstoragekeys.features.reception_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.EquipNotFoundException
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.features.reception_screen.mvi.ReceptionScreenEffect
import com.adden00.tkstoragekeys.features.reception_screen.mvi.ReceptionScreenEvent
import com.adden00.tkstoragekeys.features.reception_screen.mvi.ReceptionScreenState
import com.adden00.tkstoragekeys.features.reception_screen.mvi.UpdateType
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

class ReceptionViewModel : ViewModel(), KoinComponent {

    private val storageRepository: StorageRepository = get()

    private val _viewState = MutableStateFlow(ReceptionScreenState())
    val viewState: StateFlow<ReceptionScreenState> get() = _viewState.asStateFlow()

    private val _viewEffect = Channel<ReceptionScreenEffect>()
    val viewEffect: Flow<ReceptionScreenEffect> = _viewEffect.receiveAsFlow()

    fun obtainEvent(viewEvent: ReceptionScreenEvent) {
        when (viewEvent) {
            is ReceptionScreenEvent.GetInfo -> {
                if (viewState.value.isSearching) return

                _viewState.update { it.copy(isSearching = true) }
                viewModelScope.launch {
                    try {
                        val equipItem = storageRepository.getItemFromTable(id = viewEvent.id)
                        _viewState.update { it.copy(currentEquipItem = equipItem, error = "", enteredSearchText = "") }
                    } catch (e: EquipNotFoundException) {
                        _viewEffect.send(ReceptionScreenEffect.ShowToast("${viewEvent.id} не найдено!"))
                        _viewState.update {
                            it.copy(
                                currentEquipItem = null,
                                error = "",
                                enteredSearchText = "",
                                isNumberNoxExistsShown = true,
                                notFoundedId = viewEvent.id
                            )
                        }
                    } catch (e: Exception) {
                        _viewEffect.send(ReceptionScreenEffect.ShowToast("Exception! ${e.message ?: "no message"}"))
                    } finally {
                        _viewState.update { it.copy(isSearching = false) }
                    }
                }

            }

            is ReceptionScreenEvent.OnSearchTextChanged -> {
                _viewState.update { it.copy(enteredSearchText = viewEvent.text) }
            }

            is ReceptionScreenEvent.UpdateInfo -> {
                _viewState.update {
                    when (viewEvent.updateType) {
                        UpdateType.MOVING -> viewState.value.copy(isMovingToPerson = true)
                        UpdateType.RETURNING -> viewState.value.copy(isReturning = true)
                        UpdateType.MOVING_NO_NEW_STORAGE -> viewState.value.copy(isMovingToNewStorage = true)
                    }
                }

                viewModelScope.launch {
                    try {
                        val newItem = storageRepository.updateItem(id = viewEvent.id, item = viewEvent.newItem)
                        _viewState.update { it.copy(currentEquipItem = newItem, enteredSearchText = "", error = "") }
                    } catch (e: EquipNotFoundException) {
                        _viewEffect.send(ReceptionScreenEffect.ShowToast("${viewEvent.id} не найдено!"))
                        _viewState.update { it.copy(currentEquipItem = null, error = "", enteredSearchText = "") }
                    } catch (e: Exception) {
                        _viewEffect.send(ReceptionScreenEffect.ShowToast("Exception! ${e.message ?: "no message"}"))
                    } finally {
                        _viewState.update {
                            when (viewEvent.updateType) {
                                UpdateType.MOVING -> viewState.value.copy(isMovingToPerson = false)
                                UpdateType.RETURNING -> viewState.value.copy(isReturning = false)
                                UpdateType.MOVING_NO_NEW_STORAGE -> viewState.value.copy(isMovingToNewStorage = false)
                            }
                        }
                    }
                }
            }

            is ReceptionScreenEvent.OnEventTextChanged -> {
                _viewState.update { it.copy(enteredEventText = viewEvent.text) }
            }

            is ReceptionScreenEvent.OnLocationTextChanged -> {
                _viewState.update { it.copy(enteredLocationText = viewEvent.text) }
            }

            is ReceptionScreenEvent.UpdateEquipItem -> {
                _viewState.update { it.copy(currentEquipItem = viewEvent.item) }
                viewModelScope.launch {
                    _viewEffect.send(ReceptionScreenEffect.ShowToast("Обновлено: ${viewEvent.item.id}"))
                }
            }

            is ReceptionScreenEvent.DismissNotExistsDialog -> {
                _viewState.update { it.copy(isNumberNoxExistsShown = false, notFoundedId = null) }
            }

        }
    }
}