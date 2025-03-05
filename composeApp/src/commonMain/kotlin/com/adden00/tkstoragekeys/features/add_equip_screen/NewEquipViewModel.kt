package com.adden00.tkstoragekeys.features.add_equip_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.EquipNotFoundException
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.features.add_equip_screen.mvi.NewEquipScreenEffect
import com.adden00.tkstoragekeys.features.add_equip_screen.mvi.NewEquipScreenEvent
import com.adden00.tkstoragekeys.features.add_equip_screen.mvi.NewEquipScreenState
import com.adden00.tkstoragekeys.utils.DateUtils
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

class NewEquipViewModel : ViewModel(), KoinComponent {

    private val storageRepository: StorageRepository = get()

    private val _viewState = MutableStateFlow(NewEquipScreenState())
    val viewState: StateFlow<NewEquipScreenState> get() = _viewState.asStateFlow()

    private val _viewEffect = Channel<NewEquipScreenEffect>()
    val viewEffect: Flow<NewEquipScreenEffect> = _viewEffect.receiveAsFlow()

    fun obtainEvent(viewEvent: NewEquipScreenEvent) {
        when (viewEvent) {
            is NewEquipScreenEvent.AddEquipItem -> {
                _viewState.update { it.copy(isAdding = true) }
                viewModelScope.launch {
                    try {
                        val id = viewState.value.enteredItem.id
                        if (id.isEmpty()) {
                            throw EquipNotFoundException
                        }
                        val equipItem = storageRepository.addItem(id = id, item = viewState.value.enteredItem.copy(date = DateUtils.getCurrentDate()))
                        if (equipItem.id.isNotEmpty()) {
                            _viewEffect.send(NewEquipScreenEffect.NavigateBack(equipItem))
                        } else {
                            throw EquipNotFoundException
                        }
                    } catch (e: EquipNotFoundException) {
                        _viewEffect.send(
                            NewEquipScreenEffect.ShowToast("Ошибка добавления! Возможно этот номер уже существует! Проверьте данные и повторите попытку")
                        )
                    } catch (e: Exception) {
                        _viewEffect.send(NewEquipScreenEffect.ShowToast("Exception! ${e.message ?: "no message"}"))
                    } finally {
                        _viewState.update { it.copy(isAdding = false) }
                    }
                }
            }

            is NewEquipScreenEvent.OnEnteredItemChange -> {
                _viewState.update {
                    it.copy(
                        enteredItem = viewEvent.item
                    )
                }
            }

            is NewEquipScreenEvent.GetFreeId -> {
                _viewState.update { it.copy(isReceivingId = true) }
                viewModelScope.launch {
                    try {
                        val id = storageRepository.getFreeId()
                        if (id.isEmpty()) {
                            throw EquipNotFoundException
                        }
                        _viewState.update { it.copy(enteredItem = viewState.value.enteredItem.copy(id = id)) }
                    } catch (e: EquipNotFoundException) {
                        _viewEffect.send(NewEquipScreenEffect.ShowToast("Ошибка получения свободного id! повторите попытку или добавьте id вручную"))
                    } catch (e: Exception) {
                        _viewEffect.send(NewEquipScreenEffect.ShowToast("Exception! ${e.message ?: "no message"}"))
                    } finally {
                        _viewState.update { it.copy(isReceivingId = false) }
                    }
                }
            }

            is NewEquipScreenEvent.UpdateEquipItem -> {
                _viewState.update { it.copy(isAdding = true) }
                viewModelScope.launch {
                    try {
                        val id = viewEvent.id
                        if (id.isEmpty()) {
                            throw EquipNotFoundException
                        }
                        val equipItem =
                            storageRepository.updateItem(id = id, item = viewState.value.enteredItem.copy(date = DateUtils.getCurrentDate()))
                        if (equipItem.id.isNotEmpty()) {
                            _viewEffect.send(NewEquipScreenEffect.NavigateBack(equipItem))
                        } else {
                            throw EquipNotFoundException
                        }
                    } catch (e: EquipNotFoundException) {
                        _viewEffect.send(
                            NewEquipScreenEffect.ShowToast("Ошибка Изменения номера! Такой номер уже существует!")
                        )
                    } catch (e: Exception) {
                        _viewEffect.send(NewEquipScreenEffect.ShowToast("Exception! ${e.message ?: "no message"}"))
                    } finally {
                        _viewState.update { it.copy(isAdding = false) }
                    }
                }
            }

            is NewEquipScreenEvent.FillStartFields -> {
                _viewState.update {
                    it.copy(
                        enteredItem = viewEvent.item,
                        updatingItemId = viewEvent.editingItemId
                    )
                }
            }
        }
    }
}