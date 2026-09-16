package com.adden00.tkstoragekeys.features.location_cleanup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.features.location_cleanup.mvi.LocationCleanupScreenEffect
import com.adden00.tkstoragekeys.features.location_cleanup.mvi.LocationCleanupScreenEvent
import com.adden00.tkstoragekeys.features.location_cleanup.mvi.LocationCleanupScreenState
import com.adden00.tkstoragekeys.features.location_cleanup.mvi.LocationGroup
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

/**
 * Разбор местоположений, записанных свободным текстом. Экран разовый: после того
 * как всё сопоставлено и выгружено в таблицу, список остаётся пустым.
 */
class LocationCleanupViewModel : ViewModel(), KoinComponent {

    private val storageRepository: StorageRepository = get()

    private val _viewState = MutableStateFlow(LocationCleanupScreenState())
    val viewState: StateFlow<LocationCleanupScreenState> = _viewState.asStateFlow()

    private val _viewEffect = Channel<LocationCleanupScreenEffect>()
    val viewEffect: Flow<LocationCleanupScreenEffect> = _viewEffect.receiveAsFlow()

    /** Пропущенные в этом сеансе: сервер о них ничего не знает. */
    private val skipped = mutableSetOf<String>()

    fun obtainEvent(event: LocationCleanupScreenEvent) {
        when (event) {
            is LocationCleanupScreenEvent.Load -> load()
            is LocationCleanupScreenEvent.BindGroup -> bind(event.group, event.userId, event.userName)
            is LocationCleanupScreenEvent.SkipGroup -> {
                skipped.add(event.group.location)
                _viewState.update { state ->
                    state.copy(groups = state.groups.filterNot { it.location == event.group.location })
                }
            }
        }
    }

    private fun load() {
        if (_viewState.value.isLoading) return
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val items = storageRepository.getUnboundItems()
                val groups = items
                    .groupBy { it.location.trim() }
                    .filterKeys { it.isNotEmpty() && it !in skipped }
                    .map { (location, groupItems) -> LocationGroup(location, groupItems) }
                    // крупные группы первыми: пять верхних закрывают больше половины работы
                    .sortedWith(compareByDescending<LocationGroup> { it.count }.thenBy { it.location })
                _viewState.update { it.copy(groups = groups, error = "") }
            } catch (e: Exception) {
                val message = e.message.orEmpty()
                _viewEffect.send(LocationCleanupScreenEffect.ShowToast("Не удалось загрузить список: $message"))
                _viewState.update { it.copy(error = message.ifEmpty { "Ошибка" }) }
            } finally {
                _viewState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun bind(group: LocationGroup, userId: String, userName: String) {
        if (_viewState.value.applyingLocation != null) return
        _viewState.update {
            it.copy(applyingLocation = group.location, applyingDone = 0, applyingTotal = group.count)
        }
        viewModelScope.launch {
            val failed = mutableListOf<com.adden00.tkstoragekeys.data.model.EquipItem>()
            for (item in group.items) {
                try {
                    storageRepository.updateItem(
                        id = item.id,
                        item = item.copy(locationUserId = userId),
                        historyAction = HISTORY_ACTION
                    )
                } catch (_: Exception) {
                    failed.add(item)
                }
                _viewState.update { it.copy(applyingDone = it.applyingDone + 1) }
            }

            _viewState.update { state ->
                val rest = state.groups.mapNotNull { g ->
                    when {
                        g.location != group.location -> g
                        // часть запросов не прошла — оставляем группу с остатком,
                        // а не делаем вид, что всё получилось
                        failed.isNotEmpty() -> g.copy(items = failed)
                        else -> null
                    }
                }
                state.copy(groups = rest, applyingLocation = null, applyingDone = 0, applyingTotal = 0)
            }

            val done = group.count - failed.size
            val message = if (failed.isEmpty()) {
                "$userName — привязано вещей: $done"
            } else {
                "Привязано $done из ${group.count}, не удалось: ${failed.size}. Попробуйте ещё раз"
            }
            _viewEffect.send(LocationCleanupScreenEffect.ShowToast(message))
        }
    }

    private companion object {
        /** Отдельное действие, чтобы разбор не выглядел в истории как сотня выдач. */
        const val HISTORY_ACTION = "СОПОСТАВЛЕНО С БАЗОЙ ЛЮДЕЙ"
    }
}
