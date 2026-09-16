package com.adden00.tkstoragekeys.features.location_cleanup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.features.location_cleanup.mvi.LocationCleanupScreenEffect
import com.adden00.tkstoragekeys.features.location_cleanup.mvi.LocationCleanupScreenEvent
import com.adden00.tkstoragekeys.features.location_cleanup.mvi.LocationGroup
import com.adden00.tkstoragekeys.features.users_search.PersonPickerSheet
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGreen
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkWhite
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_back

/**
 * Разбор местоположений, записанных свободным текстом: выбрать человека один раз
 * на всю группу одинаковых записей.
 */
@Composable
fun LocationCleanupScreen(
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    val viewModel: LocationCleanupViewModel = koinViewModel()
    val state = viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pickingGroup by remember { mutableStateOf<LocationGroup?>(null) }

    LaunchedEffect("load") {
        viewModel.obtainEvent(LocationCleanupScreenEvent.Load)
    }

    LaunchedEffect("side effects") {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is LocationCleanupScreenEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    pickingGroup?.let { group ->
        PersonPickerSheet(
            onDismiss = { pickingGroup = null },
            // весь текст целиком: поиск объединяет слова по И, поэтому "Тереничев Дима"
            // Дмитрия не найдёт — лишние слова стираются на месте, курсор уже в конце строки
            initialQuery = group.location,
            onOpenDetails = { userId -> navigator.push(Screens.PersonDetails(userId)) },
            onPicked = { pick ->
                pickingGroup = null
                // пустой userId — человек вписал текст руками; для разбора это не привязка
                if (pick.userId.isNotBlank()) {
                    viewModel.obtainEvent(
                        LocationCleanupScreenEvent.BindGroup(group, pick.userId, pick.name)
                    )
                }
            }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.PaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedIconButton(onClick = { navigator.pop() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "back",
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Разбор местоположений",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    )
                    if (state.value.groups.isNotEmpty()) {
                        Text(
                            text = "${state.value.groups.size} записей · ${state.value.totalItems} вещей",
                            style = TextStyle(fontSize = 13.sp, color = TkGrey)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            when {
                state.value.isLoading && state.value.groups.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TkMain)
                    }
                }

                state.value.groups.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Всё разобрано",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Не забудьте выгрузить в таблицу — иначе привязки не переживут " +
                                    "следующий импорт.",
                            style = TextStyle(fontSize = 14.sp, color = TkGrey, textAlign = TextAlign.Center)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = Dimens.PaddingHorizontal,
                            vertical = 4.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.value.groups, key = { it.location }) { group ->
                            GroupRow(
                                group = group,
                                isApplying = state.value.applyingLocation == group.location,
                                appliedCount = state.value.applyingDone,
                                totalCount = state.value.applyingTotal,
                                enabled = state.value.applyingLocation == null,
                                onPick = { pickingGroup = group },
                                onSkip = { viewModel.obtainEvent(LocationCleanupScreenEvent.SkipGroup(group)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupRow(
    group: LocationGroup,
    isApplying: Boolean,
    appliedCount: Int,
    totalCount: Int,
    enabled: Boolean,
    onPick: () -> Unit,
    onSkip: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
        color = TkWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, TkGrey.copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.weight(1f)
                        .clickable(enabled = enabled, onClick = onPick)
                ) {
                    Text(
                        text = group.location,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = itemsLabel(group.count),
                        style = TextStyle(fontSize = 13.sp, color = TkGrey)
                    )
                }
                if (isApplying) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$appliedCount из $totalCount",
                            style = TextStyle(fontSize = 13.sp, color = TkMain)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = TkMain,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
            if (!isApplying) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onSkip, enabled = enabled) {
                        Text("Пропустить", color = TkGrey, style = TextStyle(fontSize = 14.sp))
                    }
                    TextButton(onClick = onPick, enabled = enabled) {
                        Text("Выбрать человека", color = TkGreen, style = TextStyle(fontSize = 14.sp))
                    }
                }
            }
        }
    }
}

private fun itemsLabel(count: Int): String {
    val tail = count % 10
    val hundred = count % 100
    val word = when {
        hundred in 11..14 -> "вещей"
        tail == 1 -> "вещь"
        tail in 2..4 -> "вещи"
        else -> "вещей"
    }
    return "$count $word"
}
