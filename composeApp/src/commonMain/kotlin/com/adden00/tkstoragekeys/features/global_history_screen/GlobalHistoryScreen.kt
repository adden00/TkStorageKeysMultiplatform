package com.adden00.tkstoragekeys.features.global_history_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.features.global_history_screen.mvi.GlobalHistoryScreenEffect
import com.adden00.tkstoragekeys.features.global_history_screen.mvi.GlobalHistoryScreenEvent
import com.adden00.tkstoragekeys.features.history_feed.historyFeed
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.ui.ScreenHeader
import com.adden00.tkstoragekeys.ui.showToast
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GlobalHistoryScreen(
    refreshRequest: Int,
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    val viewModel: GlobalHistoryViewModel = koinViewModel()
    val state = viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var handledRefresh by rememberSaveable { mutableIntStateOf(-1) }
    LaunchedEffect(refreshRequest) {
        if (handledRefresh != refreshRequest) {
            handledRefresh = refreshRequest
            viewModel.obtainEvent(GlobalHistoryScreenEvent.Refresh)
            listState.scrollToItem(0)
        } else {
            viewModel.obtainEvent(GlobalHistoryScreenEvent.LoadIfEmpty)
        }
    }

    LaunchedEffect("side effects") {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is GlobalHistoryScreenEffect.OpenItem -> navigator.push(Screens.Reception(startItem = effect.item))
                is GlobalHistoryScreenEffect.ShowToast -> showToast(snackbarHostState, effect.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ScreenHeader(title = "История") {
                Box(modifier = Modifier.width(96.dp), contentAlignment = Alignment.CenterEnd) {
                    if (state.value.openingItemId != null || state.value.history.isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TkMain, strokeWidth = 2.dp)
                    } else {
                        TextButton(onClick = {
                            viewModel.obtainEvent(GlobalHistoryScreenEvent.Refresh)
                            scope.launch { listState.scrollToItem(0) }
                        }) { Text("Обновить", color = TkMain) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                historyFeed(
                    state = state.value.history,
                    onLoadMore = { viewModel.obtainEvent(GlobalHistoryScreenEvent.LoadMore) },
                    onRetry = { viewModel.obtainEvent(GlobalHistoryScreenEvent.Retry) },
                    onEntryClick = { viewModel.obtainEvent(GlobalHistoryScreenEvent.OpenItem(it)) },
                )
            }
        }
    }
}
