package com.adden00.tkstoragekeys.features.item_history_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.data.model.ItemHistoryEntry
import com.adden00.tkstoragekeys.features.history_feed.historyRows
import com.adden00.tkstoragekeys.features.item_history_screen.mvi.ItemHistoryScreenEffect
import com.adden00.tkstoragekeys.features.item_history_screen.mvi.ItemHistoryScreenEvent
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.ui.ScreenHeader
import com.adden00.tkstoragekeys.ui.showToast
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ItemHistoryScreen(
    itemId: String,
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    val viewModel: ItemHistoryViewModel = koinViewModel()
    val state = viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect("load") {
        viewModel.obtainEvent(ItemHistoryScreenEvent.LoadHistory(itemId))
    }

    LaunchedEffect("side effects") {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is ItemHistoryScreenEffect.ShowToast -> {
                    showToast(snackbarHostState, effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ScreenHeader(
                title = "История #$itemId",
                onBack = { navigator.pop() },
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.value.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TkMain)
                    }
                }

                state.value.entries.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "История пуста",
                            style = TextStyle(fontSize = 16.sp, fontStyle = FontStyle.Italic),
                        )
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        historyRows(state.value.entries.newestFirst(), showItem = false)
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

private fun List<ItemHistoryEntry>.newestFirst(): List<ItemHistoryEntry> =
    if (all { it.mongoId.isNotBlank() }) sortedByDescending { it.mongoId } else reversed()
