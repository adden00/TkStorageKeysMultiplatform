package com.adden00.tkstoragekeys.features.people_search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.EquipNotFoundException
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.PeopleSearchScreenEffect
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.PeopleSearchScreenEvent
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.PeopleSearchScreenState
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

class PeopleSearchViewModel : ViewModel(), KoinComponent {

    private val storageRepository: StorageRepository = get()

    private val _viewState = MutableStateFlow(PeopleSearchScreenState())
    val viewState: StateFlow<PeopleSearchScreenState> get() = _viewState.asStateFlow()

    private val _viewEffect = Channel<PeopleSearchScreenEffect>()
    val viewEffect: Flow<PeopleSearchScreenEffect> = _viewEffect.receiveAsFlow()

    fun obtainEvent(viewEvent: PeopleSearchScreenEvent) {
        when (viewEvent) {
            PeopleSearchScreenEvent.DismissNotExistsDialog -> TODO()

            is PeopleSearchScreenEvent.GetInfo -> {
                if (viewState.value.isSearching) return

                _viewState.update { it.copy(isSearching = true) }
                viewModelScope.launch {
                    try {
                        val equipItems = storageRepository.searchByName(query = viewEvent.query)
                        _viewState.update { it.copy(currentEquipList = equipItems) }
                    } catch (e: EquipNotFoundException) {
                        if (!e.message.isNullOrEmpty()) {
                            _viewEffect.send(PeopleSearchScreenEffect.ShowToast(e.message))
                            _viewState.update { it.copy(currentEquipList = listOf(), enteredSearchText = "") }
                        } else {
                            _viewEffect.send(PeopleSearchScreenEffect.ShowToast(" не найдено!"))
                            _viewState.update {
                                it.copy(
                                    currentEquipList = listOf(),
                                    enteredSearchText = "",
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _viewEffect.send(PeopleSearchScreenEffect.ShowToast("Exception! ${e.message ?: "no message"}"))
                    } finally {
                        _viewState.update { it.copy(isSearching = false) }
                    }
                }
            }

            is PeopleSearchScreenEvent.OnSearchTextChanged -> {
                _viewState.update { it.copy(enteredSearchText = viewEvent.text) }
            }
            is PeopleSearchScreenEvent.ReturnItem -> {
                _viewState.update { it.copy(isReturning = true) }

                viewModelScope.launch {
                    try {
                        val newItem = storageRepository.updateItem(id = viewEvent.item.id, item = viewEvent.item)
                        _viewState.update {
                            it.copy(
                                currentEquipList = _viewState.value.currentEquipList.map { item -> if (item.id == newItem.id) newItem else item },
                                enteredSearchText = ""
                            )
                        }
                    } catch (e: EquipNotFoundException) {
                        if (!e.message.isNullOrEmpty()) {
                            _viewEffect.send(PeopleSearchScreenEffect.ShowToast(e.message))
                        }

                    } catch (e: Exception) {
                        _viewEffect.send(PeopleSearchScreenEffect.ShowToast("Exception! ${e.message ?: "no message"}"))
                    } finally {
                        _viewState.update { it.copy(isReturning = false) }
                    }
                }

            }
        }
    }
}
