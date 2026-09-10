package com.adden00.tkstoragekeys.features.item_history_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.data.model.ItemHistoryEntry
import com.adden00.tkstoragekeys.features.item_history_screen.mvi.ItemHistoryScreenEffect
import com.adden00.tkstoragekeys.features.item_history_screen.mvi.ItemHistoryScreenEvent
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGreen
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkWhite
import com.adden00.tkstoragekeys.theme.TkYellow
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_back

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
                    snackbarHostState.showSnackbar(effect.message)
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
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedIconButton(onClick = { navigator.pop() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "back",
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "История #$itemId",
                    style = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

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
                        items(state.value.entries.reversed()) { entry ->
                            HistoryEntryCard(entry = entry)
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryEntryCard(entry: ItemHistoryEntry) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingHorizontal, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = TkWhite,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.action,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = if (entry.action == "ДОБАВЛЕНО") TkGreen else TkYellow,
                    ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = entry.timestamp,
                    style = TextStyle(fontSize = 13.sp),
                )
            }
            if (entry.keyholderName.isNotEmpty()) {
                Text(
                    text = "Ключник: ${entry.keyholderName}",
                    style = TextStyle(fontSize = 13.sp),
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (entry.location.isNotEmpty()) {
                Text(
                    text = "Местоположение: ${entry.location}",
                    style = TextStyle(fontSize = 13.sp),
                )
            }
            if (entry.event.isNotEmpty()) {
                Text(
                    text = "Мероприятие: ${entry.event}",
                    style = TextStyle(fontSize = 13.sp),
                )
            }
        }
    }
}
