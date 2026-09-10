package com.adden00.tkstoragekeys.features.users_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.features.users_search.mvi.UsersSearchState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/** Поиск по справочнику людей по мере ввода. Используется и вкладкой "Люди", и шторкой выбора. */
class UsersSearchViewModel : ViewModel(), KoinComponent {

    private val storageRepository: StorageRepository = get()

    private val _viewState = MutableStateFlow(UsersSearchState())
    val viewState: StateFlow<UsersSearchState> = _viewState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _viewState.update { it.copy(query = query) }
        searchJob?.cancel()

        val trimmed = query.trim()
        if (trimmed.length < MIN_QUERY_LENGTH) {
            _viewState.update {
                it.copy(
                    users = emptyList(),
                    isLoading = false,
                    isSearched = false,
                    hint = if (trimmed.isEmpty()) null else "Введите минимум $MIN_QUERY_LENGTH символа"
                )
            }
            return
        }

        _viewState.update { it.copy(isLoading = true, isSearched = false) }
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            // состояние трогаем только в ветках результата: отменённый поиск не должен
            // сбросить isLoading, который уже выставил следующий
            try {
                val result = storageRepository.searchUsers(trimmed)
                _viewState.update {
                    it.copy(users = result.users, hint = result.message, isLoading = false, isSearched = true)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        users = emptyList(),
                        hint = "Ошибка поиска: ${e.message.orEmpty()}",
                        isLoading = false,
                        isSearched = false
                    )
                }
            }
        }
    }

    fun reset() {
        searchJob?.cancel()
        _viewState.value = UsersSearchState()
    }

    private companion object {
        const val MIN_QUERY_LENGTH = 2
        const val DEBOUNCE_MS = 300L
    }
}
