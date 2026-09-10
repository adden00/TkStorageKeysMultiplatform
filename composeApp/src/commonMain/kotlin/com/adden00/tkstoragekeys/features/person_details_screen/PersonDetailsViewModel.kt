package com.adden00.tkstoragekeys.features.person_details_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.EquipNotFoundException
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.features.person_details_screen.mvi.PersonDetailsScreenEffect
import com.adden00.tkstoragekeys.features.person_details_screen.mvi.PersonDetailsScreenEvent
import com.adden00.tkstoragekeys.features.person_details_screen.mvi.PersonDetailsScreenState
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class PersonDetailsViewModel : ViewModel(), KoinComponent {

    private val storageRepository: StorageRepository = get()

    private val _viewState = MutableStateFlow(PersonDetailsScreenState())
    val viewState: StateFlow<PersonDetailsScreenState> = _viewState.asStateFlow()

    private val _viewEffect = Channel<PersonDetailsScreenEffect>()
    val viewEffect: Flow<PersonDetailsScreenEffect> = _viewEffect.receiveAsFlow()

    fun obtainEvent(event: PersonDetailsScreenEvent) {
        when (event) {
            is PersonDetailsScreenEvent.Load -> {
                if (_viewState.value.isLoading) return
                _viewState.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    try {
                        // coroutineScope, чтобы ошибка async дошла до catch, а не уронила viewModelScope
                        val (user, items) = coroutineScope {
                            val user = async { storageRepository.getUser(event.userId) }
                            val items = async { storageRepository.getUserItems(event.userId) }
                            user.await() to items.await()
                        }
                        _viewState.update { it.copy(user = user, items = items, isNotFound = false) }
                    } catch (e: EquipNotFoundException) {
                        _viewState.update { it.copy(user = null, items = emptyList(), isNotFound = true) }
                    } catch (e: Exception) {
                        _viewEffect.send(PersonDetailsScreenEffect.ShowToast("Ошибка загрузки: ${e.message ?: ""}"))
                    } finally {
                        _viewState.update { it.copy(isLoading = false) }
                    }
                }
            }

            is PersonDetailsScreenEvent.ReturnItem -> {
                if (_viewState.value.isReturning) return
                _viewState.update { it.copy(isReturning = true) }
                viewModelScope.launch {
                    try {
                        storageRepository.updateItem(id = event.item.id, item = event.item)
                        // вещь переехала на склад — у человека её больше нет
                        val items = storageRepository.getUserItems(event.userId)
                        _viewState.update { it.copy(items = items) }
                        _viewEffect.send(PersonDetailsScreenEffect.ShowToast("${event.item.id} возвращено на склад"))
                    } catch (e: EquipNotFoundException) {
                        _viewEffect.send(PersonDetailsScreenEffect.ShowToast(e.message ?: "${event.item.id} не найдено!"))
                    } catch (e: Exception) {
                        _viewEffect.send(PersonDetailsScreenEffect.ShowToast("Exception! ${e.message ?: "no message"}"))
                    } finally {
                        _viewState.update { it.copy(isReturning = false) }
                    }
                }
            }
        }
    }
}
