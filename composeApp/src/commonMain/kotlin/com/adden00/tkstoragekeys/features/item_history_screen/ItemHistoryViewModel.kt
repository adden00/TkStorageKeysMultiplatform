package com.adden00.tkstoragekeys.features.item_history_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.features.item_history_screen.mvi.ItemHistoryScreenEffect
import com.adden00.tkstoragekeys.features.item_history_screen.mvi.ItemHistoryScreenEvent
import com.adden00.tkstoragekeys.features.item_history_screen.mvi.ItemHistoryScreenState
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

class ItemHistoryViewModel : ViewModel(), KoinComponent {

    private val storageRepository: StorageRepository = get()

    private val _viewState = MutableStateFlow(ItemHistoryScreenState())
    val viewState: StateFlow<ItemHistoryScreenState> = _viewState.asStateFlow()

    private val _viewEffect = Channel<ItemHistoryScreenEffect>()
    val viewEffect: Flow<ItemHistoryScreenEffect> = _viewEffect.receiveAsFlow()

    fun obtainEvent(event: ItemHistoryScreenEvent) {
        when (event) {
            is ItemHistoryScreenEvent.LoadHistory -> {
                if (_viewState.value.isLoading) return
                _viewState.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    try {
                        val entries = storageRepository.getItemHistory(event.itemId)
                        _viewState.update { it.copy(entries = entries, error = "") }
                    } catch (e: Exception) {
                        _viewEffect.send(ItemHistoryScreenEffect.ShowToast("Ошибка загрузки истории: ${e.message ?: ""}"))
                        _viewState.update { it.copy(error = e.message ?: "Ошибка") }
                    } finally {
                        _viewState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }
}
