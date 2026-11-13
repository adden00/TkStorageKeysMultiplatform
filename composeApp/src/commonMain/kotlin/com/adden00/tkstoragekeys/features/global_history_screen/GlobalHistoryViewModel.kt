package com.adden00.tkstoragekeys.features.global_history_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.features.global_history_screen.mvi.GlobalHistoryScreenEffect
import com.adden00.tkstoragekeys.features.global_history_screen.mvi.GlobalHistoryScreenEvent
import com.adden00.tkstoragekeys.features.global_history_screen.mvi.GlobalHistoryScreenState
import com.adden00.tkstoragekeys.features.history_feed.HistoryItemLookup
import com.adden00.tkstoragekeys.features.history_feed.HistoryPager
import com.adden00.tkstoragekeys.features.history_feed.lookupHistoryItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class GlobalHistoryViewModel : ViewModel(), KoinComponent {

    private val storageRepository: StorageRepository = get()

    private val _viewState = MutableStateFlow(GlobalHistoryScreenState())
    val viewState: StateFlow<GlobalHistoryScreenState> = _viewState.asStateFlow()

    private val _viewEffect = Channel<GlobalHistoryScreenEffect>()
    val viewEffect: Flow<GlobalHistoryScreenEffect> = _viewEffect.receiveAsFlow()

    private val pager = HistoryPager(viewModelScope) { before -> storageRepository.getHistory(before) }

    init {
        pager.state.onEach { history -> _viewState.update { it.copy(history = history) } }.launchIn(viewModelScope)
    }

    fun obtainEvent(event: GlobalHistoryScreenEvent) {
        when (event) {
            GlobalHistoryScreenEvent.LoadIfEmpty -> {
                val history = pager.state.value
                if (history.entries.isEmpty() && !history.isLoading) pager.refresh()
            }

            GlobalHistoryScreenEvent.Refresh -> pager.refresh()
            GlobalHistoryScreenEvent.LoadMore -> pager.loadMore()
            GlobalHistoryScreenEvent.Retry -> pager.retry()

            is GlobalHistoryScreenEvent.OpenItem -> {
                if (_viewState.value.openingItemId != null || event.itemId.isBlank()) return
                _viewState.update { it.copy(openingItemId = event.itemId) }
                viewModelScope.launch {
                    val lookup = storageRepository.lookupHistoryItem(event.itemId)
                    _viewState.update { it.copy(openingItemId = null) }
                    _viewEffect.send(
                        when (lookup) {
                            is HistoryItemLookup.Found -> GlobalHistoryScreenEffect.OpenItem(lookup.item)
                            is HistoryItemLookup.Failed -> GlobalHistoryScreenEffect.ShowToast(lookup.message)
                        }
                    )
                }
            }
        }
    }
}
