package com.adden00.tkstoragekeys.features.history_feed

import com.adden00.tkstoragekeys.data.model.HistoryPage
import com.adden00.tkstoragekeys.data.model.ItemHistoryEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryFeedState(
    val entries: List<ItemHistoryEntry> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
)

class HistoryPager(
    private val scope: CoroutineScope,
    private val load: suspend (before: String?) -> HistoryPage,
) {
    private val _state = MutableStateFlow(HistoryFeedState())
    val state: StateFlow<HistoryFeedState> = _state.asStateFlow()

    private var job: Job? = null

    fun refresh() {
        job?.cancel()
        _state.update { it.copy(isLoading = true, isRefreshing = true, error = null) }
        job = scope.launch { fetch(before = null, append = false) }
    }

    fun loadMore() {
        val current = _state.value
        if (current.isLoading || !current.hasMore || current.error != null) return
        val cursor = current.entries.lastOrNull()?.mongoId ?: return refresh()
        _state.update { it.copy(isLoading = true) }
        job = scope.launch { fetch(before = cursor, append = true) }
    }

    fun retry() {
        _state.update { it.copy(error = null) }
        if (_state.value.entries.isEmpty()) refresh() else loadMore()
    }

    private suspend fun fetch(before: String?, append: Boolean) {
        try {
            val page = load(before)
            _state.update {
                it.copy(
                    entries = if (append) it.entries + page.entries else page.entries,
                    hasMore = page.hasMore,
                    isLoading = false,
                    isRefreshing = false,
                )
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, isRefreshing = false, error = e.message ?: "Ошибка загрузки истории") }
        }
    }
}
